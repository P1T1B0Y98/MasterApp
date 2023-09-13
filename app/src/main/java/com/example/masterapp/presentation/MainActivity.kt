package com.example.masterapp.presentation

import AuthManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.example.masterapp.presentation.navigation.Screen

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController // Declare navController as a property

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainContent()

            handleDeepLink(intent)
        }
    }

    @Composable
    fun MainContent() {
        navController = rememberNavController() // Initialize navController here

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
            Log.i("MainActivity", "Deep link: $deepLinkUri")   
            when (deepLinkUri.toString()) {
                "myapp://assessment" -> {
                    navController.navigate(Screen.AssessmentScreen.route)
                }
            }
        }
    }
}

