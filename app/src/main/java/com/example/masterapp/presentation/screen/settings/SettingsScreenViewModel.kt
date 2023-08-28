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
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.masterapp.data.HealthConnectManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val healthConnectManager: HealthConnectManager,
) : ViewModel() {

    private val permissions = healthConnectManager.permissions

    val isPermissionGranted: Flow<Boolean> = healthConnectManager.isPermissionGranted

    var uiState: UiState by mutableStateOf(UiState.RequestPermissions)

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    init {
        observePermissions()
    }

    private fun observePermissions() {
        isPermissionGranted.onEach { granted ->
            if (granted) {
                uiState = UiState.RevokePermissions
            } else {
                uiState = UiState.RequestPermissions
            }
        }.launchIn(viewModelScope)
    }

    fun refreshUI() {

        viewModelScope.launch {
            val granted = hasAllPermissions()
            Log.i("SettingsScreenViewModel", "refreshUI: $granted")
            if (granted) {
                uiState = UiState.RevokePermissions
            } else {
                uiState = UiState.RequestPermissions
            }
        }
    }

    suspend fun hasAllPermissions(): Boolean {
        return healthConnectManager.hasAllPermissions(permissions)
    }

    fun revokePermissions() {
        viewModelScope.launch {
            healthConnectManager.revokeAllPermissions()
        }
        // No need to set isPermissionGranted.value = false here.
        // This should be handled by the HealthConnectManager and automatically propagated.
    }
}


sealed class UiState {
    object RequestPermissions : UiState()
    object RevokePermissions : UiState()
}

