package com.example.masterapp.presentation

import AuthManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainContent()

            // Optionally handle the deep link if you need to perform actions outside of NavController's automatic handling
            handleDeepLink(intent)
        }
    }

    @Composable
    fun MainContent() {
        val navController = rememberNavController()

        MasterApp(
            navController = navController, // Pass NavController to MasterApp
            healthConnectManager = (application as BaseApplication).healthConnectManager,
            authManager = AuthManager,
            notificationSettingsManager = (application as BaseApplication).notificationSettingsManager
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        // If NavController's built-in deep link handling isn't enough, use this method
        // For example, if you need to process data from the deep link before navigating
        val deepLinkUri = intent?.data
        if (deepLinkUri != null) {
            // Handle or process the URI as needed
        }
    }
}
