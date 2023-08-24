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
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.apollographql.apollo3.ApolloClient
import com.example.masterapp.NotificationWorker
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.roomDatabase.AnswerDatabase
import java.util.concurrent.TimeUnit

class BaseApplication : Application() {
    lateinit var apolloClient: ApolloClient

    val healthConnectManager by lazy {
        HealthConnectManager(this)
    }

    val answerDatabase: AnswerDatabase by lazy {
        AnswerDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()

        AuthManager.initialize(this)
        apolloClient = ApolloClient.Builder()
            .serverUrl("http://192.168.0.109:8080/api") // Use the dynamically retrieved IP address
            .addHttpInterceptor(AuthorizationInterceptor(AuthManager))
            .build()

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
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("YOUR_CHANNEL_ID", "Your Channel Name", importance).apply {
                description = "Your Channel Description"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification() {
        val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueue(dailyWorkRequest)
    }

}



