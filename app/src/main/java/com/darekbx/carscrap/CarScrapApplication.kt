package com.darekbx.carscrap

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.darekbx.carscrap.di.appModule
import com.darekbx.carscrap.di.domainModule
import com.darekbx.carscrap.di.viewModelModule
import com.darekbx.carscrap.worker.DataRefreshWorker
import com.darekbx.carscrap.worker.KoinWorkerFactory
import com.google.firebase.FirebaseApp
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import java.util.concurrent.TimeUnit

class CarScrapApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        startKoin {
            androidLogger()
            androidContext(this@CarScrapApplication)
            modules(appModule, domainModule, viewModelModule)
        }

        val workerFactory: KoinWorkerFactory by inject()
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )

        schedulePeriodicDataRefresh()
    }

    private fun schedulePeriodicDataRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dataRefreshWorkRequest = PeriodicWorkRequestBuilder<DataRefreshWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DataRefreshWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dataRefreshWorkRequest
        )
    }
}
