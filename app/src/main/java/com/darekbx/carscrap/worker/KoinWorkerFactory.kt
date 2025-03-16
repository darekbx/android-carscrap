package com.darekbx.carscrap.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.darekbx.carscrap.repository.local.dao.FilterDao
import com.darekbx.carscrap.repository.remote.scrap.FilterFetch

class KoinWorkerFactory(
    val filterFetch: FilterFetch,
    val filterDao: FilterDao
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            DataRefreshWorker::class.java.name -> {
                DataRefreshWorker(appContext, workerParameters, filterFetch, filterDao)
            }

            else -> null
        }
    }
}
