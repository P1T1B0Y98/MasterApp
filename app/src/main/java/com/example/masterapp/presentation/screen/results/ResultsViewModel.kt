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
import com.example.masterapp.QUESTIONNAIRES_RESPONSE_BY_USERQuery
import com.example.masterapp.QUESTIONNAIRES_RESPONSE_DELETEMutation
import com.example.masterapp.data.EncryptionHelper.decrypt
import com.example.masterapp.data.FHIRQuestionnaireResponseItem
import com.example.masterapp.data.roomDatabase.QuestionnaireReminderViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ResultsViewModel(
    private val questionnaireReminderViewModel: QuestionnaireReminderViewModel,
    private val apolloClient: ApolloClient,

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

    fun onAnswerSelected(answer: AssessmentResponses) {
        Log.i("ResultsViewModel", "Setting detailed state for answer: ${answer.questionnaire.title}")
        _uiState.value = ResultsUiState.Detailed(answer)
    }

    private suspend fun getAssessmentsByUser(): List<AssessmentResponses>? {
        val userId = AuthManager.getUserId()
        Log.i("UserId:", userId)
        try {
            val query = QUESTIONNAIRES_RESPONSE_BY_USERQuery(
                userID = AuthManager.getUserId(),
                limit =  Optional.Absent,
                offset = Optional.Absent
            )

            val response = apolloClient.query(query).execute()
            Log.i("response", response.toString())
            val assessmentResponses = response.data?.questionnaireResponseListByUserID?.rows ?: emptyList()
            Log.i("asssessments", assessmentResponses.toString())
            val decryptedAssessmentResponses = assessmentResponses.map { response ->
                val formData = response.item as? String
                if (formData != null) {
                    val decryptedFormData = decrypt(formData)
                    Log.i("Decrypted", "Decrypted form data: $decryptedFormData")
                    response.copy(item = decryptedFormData)
                    Log.i("AssessmentsList", "Assessment response: $response")
                    val json = Json { ignoreUnknownKeys = true}
                    val item = json.decodeFromString<List<FHIRQuestionnaireResponseItem>>(decryptedFormData)
                    Log.i("AssessmentsList", "Form data: $formData")
                    val formattedTime = ZonedDateTime.parse(response.authored)
                    val format = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")
                    val formattedDate = formattedTime.format(format)
                    AssessmentResponses(
                        id = response.id,
                        questionnaire = QuestionnaireInfo(
                            id = response.questionnaire.id,
                            title = response.questionnaire.title,
                            questionnaireType = response.questionnaire.type
                        ),
                        item = item,
                        authored = formattedDate
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
    }

    suspend fun deleteQuestionnaireResponse(id: String): Boolean {
        Log.i("id", id)
        try {
            val mutation = QUESTIONNAIRES_RESPONSE_DELETEMutation(
                id = id
            )

            val response = apolloClient.mutation(mutation).execute()

            Log.i(response.toString(), "response")

        } catch (e: ApolloException) {
            Log.w("AssessmentsList", "Failed to get response list", e)
        }
        getAssessmentsByUser()
        questionnaireReminderViewModel.deleteReminder(AuthManager.getUserId(), id)
        return false
    }

    fun goBack() {
        Log.i("ResultsViewModel", "goBack() invoked")
        _uiState.value = ResultsUiState.Success(assessmentResponses ?: emptyList())
    }

}
