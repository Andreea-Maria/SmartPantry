package com.example.smartpantry.presentation.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartpantry.R

class ExpiryCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context,workerParams) {

    override suspend fun doWork(): Result {
        showNotification()

        return Result.success()
    }

    private fun showNotification(){

        val notificationManager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channelId = "expiry_channel"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Expiry Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
            .setContentTitle("Smart Pantry")
            .setContentText("Check products that may expire soon")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            1,
            notification
        )

    }
}