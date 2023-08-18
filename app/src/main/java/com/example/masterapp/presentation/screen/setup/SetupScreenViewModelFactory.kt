package com.example.masterapp.presentation.screen.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.presentation.screen.SharedViewModel

class SetupScreenViewModelFactory(
    private val healthConnectManager: HealthConnectManager,
    private val navController: NavController,
    private val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetupScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SetupScreenViewModel(healthConnectManager, navController, sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
