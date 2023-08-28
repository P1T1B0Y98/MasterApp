package com.example.masterapp

import AuthManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.masterapp.data.roomDatabase.AnswerDatabase
import com.example.masterapp.data.roomDatabase.QuestionnaireReminder
import com.example.masterapp.data.roomDatabase.QuestionnaireReminderDao
import com.example.masterapp.type.AssessmentsPage

class NotificationWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {

    private val questionnaireReminderDao: QuestionnaireReminderDao =
        AnswerDatabase.getDatabase(appContext).questionnaireReminderDao
    override suspend fun doWork(): Result {
        checkAndSendNotifications()
        return Result.success()
    }

    private suspend fun retrieveAllEntries(): List<QuestionnaireReminder> {
        return questionnaireReminderDao.getRemindersForUser(AuthManager.getUserId()) // replace USER_ID with the appropriate user id
    }
    private fun sendNotification() {
        // Intent with the deep link URI
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("myapp://assessments"))
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(applicationContext, "REMINDER_CHANNEL_ID")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Questionnaire Reminder")
            .setContentText("You can finally answer one of your questionnaires again!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        try {
            notificationManager.notify(200, builder.build())
        } catch (e: SecurityException) {
            Log.i("Hello", "SecurityException: ${e.message}")
        }
    }



    private suspend fun checkAndSendNotifications() {
        // Retrieve all entries
        val entries = retrieveAllEntries() // Implement this function to get all entries from your database

        val currentTime = System.currentTimeMillis()

        for (entry in entries) {
            val nextQuestionnaireTime = currentTime - entry.notificationTimestamp // This assumes each entry has a timestamp indicating when the user can answer again

            if (currentTime >= nextQuestionnaireTime!!) {
                sendNotification() // You might want to show a specific notification for each entry, or just a generic one
            }
        }
    }
}

