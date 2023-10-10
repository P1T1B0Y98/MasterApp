package com.example.masterapp.presentation

import AuthManager
import AuthorizationInterceptor
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.apollographql.apollo3.ApolloClient
import com.example.masterapp.NotificationWorker
import com.example.masterapp.NotificationSettingsManager
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.roomDatabase.QuestionnaireReminderDatabase
import com.example.masterapp.presentation.screen.setup.PreferencesHelper
import java.util.concurrent.TimeUnit

class BaseApplication : Application() {
    lateinit var apolloClient: ApolloClient

    lateinit var preferencesHelper: PreferencesHelper

    val healthConnectManager by lazy {
        HealthConnectManager(this)
    }

    val notificationSettingsManager by lazy {
        NotificationSettingsManager(this)
    }

    val questionnaireReminderDatabase: QuestionnaireReminderDatabase by lazy {
        QuestionnaireReminderDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        AuthManager.initialize(this)
        apolloClient = ApolloClient.Builder()
            .serverUrl("http://192.168.0.109:8080/api") // Use the dynamically retrieved IP address
            .addHttpInterceptor(AuthorizationInterceptor(AuthManager))
            .build()
        scheduleNotification()
        preferencesHelper = PreferencesHelper(this)
    }

    private fun getServerUrlFromWifi(): String {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val linkAddress = connectivityManager?.getLinkProperties(connectivityManager.activeNetwork)?.linkAddresses?.first()
        val ipAddress = linkAddress?.address?.hostAddress
        Log.i("BaseApplication", "ipAddress: $ipAddress")
        return "http://$ipAddress:8080/api"

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Channel for Reminder Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("REMINDER_CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification() {
        Log.i("BaseApplication", "scheduleNotification")
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}
