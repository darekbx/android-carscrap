package com.darekbx.carscrap.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.darekbx.carscrap.repository.local.CacheDatabase
import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.remote.RemoteData
import com.darekbx.carscrap.repository.remote.TimeProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "car_scrap_preferences")

val appModule = module {
    single<CacheDatabase> {
        Room
            .databaseBuilder(get<Application>(), CacheDatabase::class.java, CacheDatabase.DB_NAME)
            .build()
    }
    single<CarModelDao> { get<CacheDatabase>().carModelDao() }
    single { TimeProvider() }
    single { androidContext().dataStore }
    factory { RemoteData(get(), get(), get()) }
}
