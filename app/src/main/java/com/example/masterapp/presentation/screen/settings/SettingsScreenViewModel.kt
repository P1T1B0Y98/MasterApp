package com.example.masterapp.presentation.screen.settings

import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.units.Mass
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.masterapp.data.HealthConnectManager
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val healthConnectManager: HealthConnectManager,
) : ViewModel() {


    var uiState: UiState by mutableStateOf(UiState.RequestPermissions)

    private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    private val permissions = healthConnectManager.permissions

    var isPermissionGranted = false


    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    init {

        viewModelScope.launch{
            isPermissionGranted = hasAllPermissions()

            uiState = if (isPermissionGranted){
                UiState.RevokePermissions
            } else {
                UiState.RequestPermissions
            }
        }

    }
    private suspend fun hasAllPermissions(): Boolean {
        return healthConnectManager.hasAllPermissions(permissions)
    }
}

sealed class UiState {
    object RequestPermissions : UiState()
    object RevokePermissions : UiState()
}

