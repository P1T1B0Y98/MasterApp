package com.example.masterapp

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Here, you can add the logic to check if the questionnaire is open for answering
        // If it is, send the notification

        // Sending a simple notification as an example:
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "YOUR_CHANNEL_ID")
            .setContentTitle("Questionnaire Reminder")
            .setContentText("It's time to answer the questionnaire!")
            .setSmallIcon(R.drawable.stress_management) // Replace with your icon
            .build()

        notificationManager.notify(1, notification)

        return Result.success()
    }
}
