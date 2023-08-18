package com.example.masterapp.presentation.screen.assessments

import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.example.masterapp.ASSESSMENTS_FINDQuery
import com.example.masterapp.GET_ASSESSMENTS_LISTQuery
import com.example.masterapp.data.Assessment
import com.example.masterapp.data.AssessmentSchema
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.Option
import com.example.masterapp.presentation.screen.setup.SetupScreenViewModel
import com.example.masterapp.type.AssessmentsFilterInput
import com.example.masterapp.type.AssessmentsOrderByEnum
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class AssessmentViewModel(
    private val apolloClient: ApolloClient,
    private val healthConnectManager: HealthConnectManager) : ViewModel() {

    var uiState: UiState by mutableStateOf(UiState.RequestPermissions)

    private val permissions = healthConnectManager.permissions

    var permissionsGranted = mutableStateOf(false)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()


    init {

        viewModelScope.launch{

            uiState = if (hasAllPermissions()){
                UiState.PermissionsAccepted
            } else {
                UiState.RequestPermissions
            }
        }
    }

    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {

            }
        }
    }
    suspend fun getAssessmentsList(
        filter: AssessmentsFilterInput?,
        limit: Int?,
        offset: Int?,
        orderBy: AssessmentsOrderByEnum?
    ): List<Assessment>? {
        try {
            val query = GET_ASSESSMENTS_LISTQuery(
                filter = if (filter != null) Optional.Present(filter) else Optional.Absent,
                limit = if (limit != null) Optional.Present(limit) else Optional.Absent,
                offset = if (offset != null) Optional.Present(offset) else Optional.Absent,
                orderBy = if (orderBy != null) Optional.Present(orderBy) else Optional.Absent
            )
            val response = apolloClient.query(query).execute()

            if (!response.hasErrors()) {
                Log.i("AssessmentsList", "Successfully got assessments list")
                val assessments = response.data?.assessmentsList?.rows?.map {
                    Assessment(
                        id = it.id,
                        title = it.title,
                        assessmentType = it.assessment_type,
                        frequency = it.frequency,
                        assessmentSchema = it.assessmentSchema?.map { schema ->
                            AssessmentSchema(
                                type = schema?.type,
                                field = schema?.field.orEmpty(),
                                question = schema?.question,
                                options = schema?.options?.map { option ->
                                    Option(
                                        field = option?.field.orEmpty(),
                                        label = option?.label.orEmpty(),
                                        value = option?.value.orEmpty() // Assuming you have a 'value' field in 'Option'
                                    )
                                }
                            )
                        }
                    )
                }

                // Printing out all assessments
                assessments?.forEach { assessment ->
                    Log.i("AssessmentDetails", "ID: ${assessment.id}, Title: ${assessment.title}, Type: ${assessment.assessmentType}, Frequency: ${assessment.frequency}")
                }

                return assessments
            } else {
                Log.w("AssessmentsList", "Failed to get assessments list")
                return null
            }
        } catch (e: ApolloException) {
            Log.w("AssessmentsList", "Failed to get assessments list", e)
            return null
        }
    }

    private suspend fun hasAllPermissions(): Boolean {
        return healthConnectManager.hasAllPermissions(permissions)
    }


    suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        permissionsGranted.value = healthConnectManager.hasAllPermissions(permissions)
        healthConnectManager.isPermissionGranted.value = permissionsGranted.value
        uiState = try {
            if (permissionsGranted.value) {
                block()
                UiState.PermissionsAccepted
            }
            else {
                UiState.RequestPermissions
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
}

sealed class UiState {
    object RequestPermissions : UiState()
    object PermissionsAccepted : UiState()
    data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()

}