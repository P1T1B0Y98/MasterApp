package com.example.masterapp.presentation

import AuthManager
import AuthorizationInterceptor
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.roomDatabase.AnswerDatabase

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
}


