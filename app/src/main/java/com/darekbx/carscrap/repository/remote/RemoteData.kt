package com.darekbx.carscrap.repository.remote

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.darekbx.carscrap.BuildConfig
import com.darekbx.carscrap.repository.SynchronizeBus
import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dto.CarModel
import com.darekbx.carscrap.repository.remote.model.Car
import com.darekbx.carscrap.repository.remote.model.Car.Companion.toCar
import com.darekbx.carscrap.utils.DateTimeFormatter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.time.measureTime

class RemoteData(
    private val dataStore: DataStore<Preferences>,
    private val carModelDao: CarModelDao,
    private val timeProvider: TimeProvider,
    private val dateTimeFormatter: DateTimeFormatter,
    private val synchronizeBus: SynchronizeBus,
    private val limit: Int? = null
) {

    sealed class SynchronizationStep(val index: Int) : Comparable<SynchronizationStep> {
        data object Authenticate : SynchronizationStep(index = 0)
        data object FetchData : SynchronizationStep(index = 1)
        data object SaveTimestamp : SynchronizationStep(index = 2)
        data object StoreData : SynchronizationStep(index = 3)
        data object Completed : SynchronizationStep(index = 4)
        data class Failed(val error: Throwable) : SynchronizationStep(index = 5)

        override fun compareTo(other: SynchronizationStep): Int {
            return this.index.compareTo(other.index)
        }
    }

    suspend fun synchronize(onSynchronizationStep: SynchronizationStep.() -> Unit) {
        val duration = measureTime {
            try {
                // 1. Authenticate
                publishStatus(onSynchronizationStep, SynchronizationStep.Authenticate)
                authenticate() ?: return

                // 2. Read last fetch timestamp
                val lastFetchTimestamp = lastFetchTimesamp() ?: 0L

                // 3. Fetch remote ids
                publishStatus(onSynchronizationStep, SynchronizationStep.FetchData)
                val cars = fetchData(lastFetchTimestamp)

                // 4. Save timestamp
                publishStatus(onSynchronizationStep, SynchronizationStep.SaveTimestamp)
                saveFetchTimestamp()

                // 5. Store data in local database
                publishStatus(onSynchronizationStep, SynchronizationStep.StoreData)
                storeData(cars)

                synchronizeBus.publishTimestamp()
                publishStatus(onSynchronizationStep, SynchronizationStep.Completed)
            } catch (e: Exception) {
                Log.e(TAG, "Synchronization failed", e)
                publishStatus(onSynchronizationStep, SynchronizationStep.Failed(e))
            }
        }
        Log.v(TAG, "Synchronization completed, took $duration")
    }

    private suspend fun publishStatus(
        onSynchronizationStep: SynchronizationStep.() -> Unit,
        status: SynchronizationStep
    ) {
        onSynchronizationStep(status)
        delay(1000)
    }

    suspend fun lastFetchDateTime(): String? {
        return lastFetchTimesamp()
            ?.let { dateTimeFormatter.formatTimestamp(it) }
    }

    /**
     * @param lastFetchTimestamp timestamp in milliseconds
     */
    private suspend fun fetchData(lastFetchTimestamp: Long): List<Car> {
        Log.v(TAG, "Start fetching data...")
        val timestamp = Timestamp(seconds = lastFetchTimestamp / 1000, nanoseconds = 0)
        var query = Firebase
            .firestore
            .collection(CARS)
            .whereGreaterThan(CREATED_AT, timestamp)

        if (limit != null) {
            query = query.limit(limit.toLong())
        }

        val metadata = query.get().await()

        Log.v(TAG, "Fetched ${metadata.documents.size} documents")
        return metadata.documents.map { it.toCar() }
    }

    private suspend fun authenticate(): FirebaseUser? {
        Log.v(TAG, "Start authentication")
        val authResult = FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(BuildConfig.CLOUD_EMAIL, BuildConfig.CLOUD_PASSWORD)
            .await()
        Log.v(TAG, "Authentication result: ${authResult.user != null}")
        return authResult.user
    }

    private suspend fun storeData(cars: List<Car>) {
        Log.v(TAG, "Start storing data")
        carModelDao.insertAll(cars.map {
            CarModel(
                externalId = "${it.externalId}",
                createdAt = it.createdAt.seconds * 1000L,
                url = it.url,
                price = it.price,
                currency = it.currency,
                fuelType = it.fuelType,
                gearbox = it.gearbox,
                enginePower = it.enginePower,
                year = it.year,
                countryOrigin = it.countryOrigin,
                mileage = it.mileage
            )
        })
    }

    private suspend fun lastFetchTimesamp(): Long? {
        return dataStore.data.map { preferences ->
            preferences[LAST_FETCH_TIMESTAMP_KEY]
        }.firstOrNull()
    }

    private suspend fun saveFetchTimestamp() {
        dataStore.edit { preferences ->
            preferences[LAST_FETCH_TIMESTAMP_KEY] = timeProvider.currentTimeMillis()
        }
    }

    companion object {
        private const val TAG = "RemoteData"
        private const val CARS = "cars"
        private const val CREATED_AT = "createdAt"

        private val LAST_FETCH_TIMESTAMP_KEY = longPreferencesKey("last_fetch_timestamp")
    }
}
