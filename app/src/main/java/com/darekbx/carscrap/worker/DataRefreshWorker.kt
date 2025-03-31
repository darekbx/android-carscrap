package com.darekbx.carscrap.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.darekbx.carscrap.R
import com.darekbx.carscrap.repository.local.dao.FilterDao
import com.darekbx.carscrap.repository.remote.scrap.FilterFetch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataRefreshWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val filterFetch: FilterFetch,
    private val filterDao: FilterDao
) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            var addedItemsCount = 0

            // 1. Fetch filters
            val filters = filterDao.getAllFilters()

            // 2. Scrap each of filters
            filters.forEach { filter ->
                filterFetch.fetch(filter.id, onCompleted = { count ->
                    addedItemsCount += count
                })
            }

            showNotification(addedItemsCount)

            Result.success()
        } catch (e: Exception) {
            Log.e("CoroutineWorker", "Error in worker", e)
            Result.failure()
        }
    }

    private fun showNotification(itemsCount: Int) {
        val context = applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Work Results",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for work results"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sync_notify)
            .setContentTitle("Sync completed")
            .setContentText("Added $itemsCount new cars")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "work_results_channel"
        const val NOTIFICATION_ID = 1
    }
}
