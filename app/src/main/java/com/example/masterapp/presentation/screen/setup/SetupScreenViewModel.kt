package com.example.masterapp.presentation.screen.setup

import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
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
import com.example.masterapp.NotificationSettingsManager
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.presentation.navigation.Screen
import com.example.masterapp.presentation.screen.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class SetupScreenViewModel(
    private val healthConnectManager: HealthConnectManager,
    private val navController: NavController,
    private val sharedViewModel: SharedViewModel,
    private val notificationSettingsManager: NotificationSettingsManager,
) : ViewModel() {

    // Live data or State to manage UI state
    var uiState: UiState by mutableStateOf(UiState.Initial)
        private set

    private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    val permissions = healthConnectManager.permissions

    var permissionsGranted = mutableStateOf(false)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    // Other properties and methods related to setup ...
    val isNotificationEnabled: Boolean
        get() = notificationSettingsManager.canShowNotifications()

    fun requestNotificationPermission() = notificationSettingsManager.openNotificationSettings()
    init {
        viewModelScope.launch {
            appInstalled()
        }
    }

    fun refreshUI() {
        viewModelScope.launch{
            appInstalled()
        }

    }

    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {

            }
        }
    }

    suspend fun appInstalled () {

        val isInstalled = healthConnectManager.isAppInstalled(
            healthConnectManager.context,
            "com.google.android.apps.healthdata"
        )

        Log.i("WelcomeScreenViewModel", "appInstalled now?: $isInstalled")
        if (isInstalled && healthConnectManager.hasAllPermissions(permissions)) {
            Log.i("WelcomeScreenViewModel", "appInstalled: ${permissionsGranted.value}")
            navController.navigate(Screen.HomeScreen.route)
        } else if(isInstalled && !permissionsGranted.value){
            uiState = UiState.PermissionsNotGranted
        } else {
            uiState = UiState.AppNotInstalled
        }
    }

    fun openAppPage() {
        healthConnectManager.openAppInPlayStoreIfNotInstalled(
            healthConnectManager.context,
            "com.google.android.apps.healthdata"
        )
    }

    /**
     * Provides permission check and error handling for Health Connect suspend function calls.
     *
     * Permissions are checked prior to execution of [block], and if all permissions aren't granted
     * the [block] won't be executed, and [permissionsGranted] will be set to false, which will
     * result in the UI showing the permissions button.
     *
     * Where an error is caught, of the type Health Connect is known to throw, [uiState] is set to
     * [UiState.Error], which results in the snackbar being used to show the error message.
     */
    suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        permissionsGranted.value = healthConnectManager.hasAllPermissions(permissions)
        healthConnectManager._isPermissionGranted.value = permissionsGranted.value
        Log.i("WelcomeScreenViewModel", "tryWithPermissionsCheck: ${permissionsGranted.value}")
        uiState = try {
            if (permissionsGranted.value) {
                block()
                UiState.Done
            }
            else {
                UiState.PermissionsNotGranted
            }
        } catch (remoteException: RemoteException) {
            UiState.Error(remoteException)
        } catch (securityException: SecurityException) {
            UiState.Error(securityException)
        } catch (ioException: IOException) {
            UiState.Error(ioException)
        } catch (illegalStateException: IllegalStateException) {
            UiState.Error(illegalStateException)
        }
    }

    sealed class UiState {
        object Initial : UiState()
        object AppNotInstalled : UiState()
        object PermissionsNotGranted : UiState()
        object Done : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
        // ... any other setup related states ...
    }
}
