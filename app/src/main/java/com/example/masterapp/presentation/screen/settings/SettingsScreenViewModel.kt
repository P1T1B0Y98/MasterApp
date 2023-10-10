package com.example.masterapp.presentation.screen.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.masterapp.QUESTIONNAIRES_RESPONSE_DELETE_ALLMutation
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.roomDatabase.QuestionnaireReminderViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val healthConnectManager: HealthConnectManager,
    private val apolloClient: ApolloClient,
    private val questionnaireReminderViewModel: QuestionnaireReminderViewModel
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

    suspend fun deleteAllQuestionnaireResponses(userId: String): Boolean? {
        Log.i("id", userId)
        try {
            val mutation = QUESTIONNAIRES_RESPONSE_DELETE_ALLMutation(
                userId = userId
            )

            val response = apolloClient.mutate(mutation).execute()

            Log.i(response.toString(), "response")

        } catch (e: ApolloException) {
            Log.w("AssessmentsList", "Failed to get response list", e)
        }
        questionnaireReminderViewModel.deleteAllReminders(userId)

        return false
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

