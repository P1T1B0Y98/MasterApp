package com.example.masterapp.presentation.screen.results

import AuthManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.example.masterapp.ASSESSMENTS_RESPONSE_BY_USERQuery
import com.example.masterapp.data.EncryptionHelper.decrypt
import com.example.masterapp.data.FormData
import com.example.masterapp.data.roomDatabase.AnswerViewModel
import com.example.masterapp.presentation.screen.SharedViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ResultsViewModel(
    private val answerViewModel: AnswerViewModel,
    private val sharedViewModel: SharedViewModel,
    private val apolloClient: ApolloClient
) : ViewModel() {

    sealed class ResultsUiState {
        object Loading : ResultsUiState()
        data class Success(val answers: List<AssessmentResponses?>) : ResultsUiState()
        data class Detailed(val answer: AssessmentResponses) : ResultsUiState()
        object Error : ResultsUiState()
    }

    private val _uiState = MutableLiveData<ResultsUiState>()
    val uiState: LiveData<ResultsUiState> = _uiState
    var assessmentResponses: List<AssessmentResponses>? by mutableStateOf(null)

    init {
        _uiState.value = ResultsUiState.Loading
        viewModelScope.launch {
            assessmentResponses = getAssessmentsByUser()
        }

    }

    /*private fun loadAssessments() {
        _uiState.value = ResultsUiState.Loading
        viewModelScope.launch {
            try {
                answerViewModel.getAllAnswersByUser(AuthManager.getUserId())
                answerViewModel.answersByUser.observeForever { answers ->
                    if (answers != null) {
                        Log.i("ResultsViewModel", "Answers: $answers")
                        _uiState.value = ResultsUiState.Success(answers)
                    } else {
                        _uiState.value = ResultsUiState.Error
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ResultsUiState.Error
            }
        }
    }*/

    fun onAnswerSelected(answer: AssessmentResponses) {
        Log.i("ResultsViewModel", "Setting detailed state for answer: ${answer.assessmentId.title}")
        _uiState.value = ResultsUiState.Detailed(answer)
    }

    private suspend fun getAssessmentsByUser(): List<AssessmentResponses>? {
        val userId = AuthManager.getUserId()
        Log.i("UserId:", userId)
        try {
            val query = ASSESSMENTS_RESPONSE_BY_USERQuery(
                userID = AuthManager.getUserId(),
                limit =  Optional.Absent,
                offset = Optional.Absent
            )

            val response = apolloClient.query(query).execute()
            val assessmentResponses = response.data?.assessmentResponseListByUserID?.rows ?: emptyList()

            val decryptedAssessmentResponses = assessmentResponses.map { response ->
                val formData = response.formData as? String
                if (formData != null) {
                    val decryptedFormData = decrypt(formData)
                    Log.i("Decrypted", "Decrypted form data: $decryptedFormData")
                    response.copy(formData = decryptedFormData)
                    Log.i("AssessmentsList", "Assessment response: $response")
                    val json = Json { ignoreUnknownKeys = true}
                    val formData = json.decodeFromString<FormData>(decryptedFormData)
                    Log.i("AssessmentsList", "Form data: $formData")
                    AssessmentResponses(
                        id = response.id,
                        assessmentId = AssessmentInfo(
                            id = response.assessmentID?.get(0)?.id ?: "",
                            title = response.assessmentID?.get(0)?.title ?: "",
                            assessmentType = response.assessmentID?.get(0)?.assessment_type
                        ),
                        formData = formData
                    )

                } else {
                    return null // In case formData is not a String, keep the original response
                }
            }
            Log.i("AssessmentsResponse", "Assessment responses: $decryptedAssessmentResponses")
            _uiState.value = ResultsUiState.Success(decryptedAssessmentResponses)
            return decryptedAssessmentResponses

        } catch (e: ApolloException) {
            Log.w("AssessmentsList", "Failed to get response list", e)
            return null
        }
        return null
    }


    fun goBack() {
        Log.i("ResultsViewModel", "goBack() invoked")
        _uiState.value = ResultsUiState.Success(assessmentResponses ?: emptyList())
    }

    // Implement other logic and functions as needed
}
