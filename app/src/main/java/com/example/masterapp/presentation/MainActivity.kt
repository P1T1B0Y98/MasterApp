package com.example.masterapp.presentation

import AuthManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MasterApp(
                healthConnectManager = (application as BaseApplication).healthConnectManager,
                authManager = AuthManager,
            )
        }
    }
}

