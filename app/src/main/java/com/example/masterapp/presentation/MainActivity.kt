package com.example.masterapp.presentation

import AuthManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.example.masterapp.presentation.navigation.Screen

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainContent()

            handleDeepLink(intent)
        }
    }

    @Composable
    fun MainContent() {
        navController = rememberNavController()

        MasterApp(
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

