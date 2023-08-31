package com.example.masterapp.presentation.screen.assessments

import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
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
import com.example.masterapp.data.roomDatabase.QuestionnaireRepository
import com.example.masterapp.presentation.screen.setup.SetupScreenViewModel
import com.example.masterapp.type.AssessmentsFilterInput
import com.example.masterapp.type.AssessmentsOrderByEnum
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class AssessmentViewModel(
    private val apolloClient: ApolloClient,
    private val healthConnectManager: HealthConnectManager,
    private val questionnaireRepository: QuestionnaireRepository) : ViewModel() {

    var uiState: UiState by mutableStateOf(UiState.Loading)

    private val permissions = healthConnectManager.permissions

    var permissionsGranted = mutableStateOf(false)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    var assessmentPair: Pair<List<Assessment>, List<Assessment>>? by mutableStateOf(null)


    init {
        viewModelScope.launch {
            if (hasAllPermissions()) {
                uiState = UiState.Loading
                // Assuming you have a userId, filter, etc. to use {
                fetchAndCategorizeAssessments(
                    userId = AuthManager.getUserId(),
                    filter = null,
                    limit = null,
                    offset = null,
                    orderBy = null
                )
            } else {
                uiState = UiState.RequestPermissions
            }
        }
    }


    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {

            }
        }
    }

    private suspend fun fetchAndCategorizeAssessments(
        userId: String,
        filter: AssessmentsFilterInput?,
        limit: Int?,
        offset: Int?,
        orderBy: AssessmentsOrderByEnum?
    ) {

        val allAssessments = getAssessmentsList(filter, limit, offset, orderBy)
        assessmentPair = allAssessments?.let { categorizeAssessments(userId, it) }
        uiState = UiState.PermissionsAccepted
    }

    private suspend fun categorizeAssessments(
        userId: String,
        assessments: List<Assessment>
    ): Pair<List<Assessment>, List<Assessment>> {
        val completedAssessments = mutableListOf<Assessment>()
        val readyAssessments = mutableListOf<Assessment>()

        for (assessment in assessments) {
            if (isQuestionnaireCompleted(userId, assessment.id)) {
                Log.i("AssessmentsList", "Assessment ${assessment.id} is completed")
                completedAssessments.add(assessment)
            } else {
                Log.i("AssessmentsList", "Assessment ${assessment.id} is ready")
                readyAssessments.add(assessment)
            }
        }

        return Pair(readyAssessments, completedAssessments)
    }


    private suspend fun getAssessmentsList(
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


    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        permissionsGranted.value = healthConnectManager.hasAllPermissions(permissions)
        healthConnectManager._isPermissionGranted.value = permissionsGranted.value
        uiState = try {
            if (permissionsGranted.value) {
                block()
                UiState.PermissionsAccepted
            } else {
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

    private suspend fun isQuestionnaireCompleted(userId: String, questionnaireID: String): Boolean {
        // Fetch record for the given userId and questionnaireId
        Log.i("QuestionnaireReminder", "Fetching questionnaire reminder for user $userId and questionnaire $questionnaireID")
        val reminder = questionnaireRepository.getQuestionnaire(userId, questionnaireID)
        Log.i("QuestionnaireReminder", "Reminder: $reminder")
        reminder?.let {
            // If completedTimestamp is null, the questionnaire hasn't been completed yet
            if (reminder == null) {
                return false
            }

            // If current time is greater than or equal to the notificationTimestamp, then the user can take the questionnaire again
            return false
        }

        return false

    }
}

sealed class UiState {
    object Loading: UiState()
    object RequestPermissions : UiState()
    object PermissionsAccepted : UiState()
    data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()

}