package com.example.masterapp.presentation.screen.results

import AuthManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masterapp.data.Assessment
import com.example.masterapp.data.roomDatabase.Answer
import com.example.masterapp.data.roomDatabase.AnswerViewModel
import com.example.masterapp.presentation.screen.SharedViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ResultsViewModel(
    private val answerViewModel: AnswerViewModel,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {

    sealed class ResultsUiState {
        object Loading : ResultsUiState()
        data class Success(val answers: List<Answer>) : ResultsUiState()
        data class Detailed(val answer: Answer) : ResultsUiState()
        object Error : ResultsUiState()
    }

    private val _uiState = MutableLiveData<ResultsUiState>()
    val uiState: LiveData<ResultsUiState> = _uiState

    init {
        loadAssessments()
    }

    private fun loadAssessments() {
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
    }

    fun onAnswerSelected(answer:  Answer) {
        Log.i("ResultsViewModel", "Setting detailed state for answer: ${answer.assessmentTitle}")
        _uiState.value = ResultsUiState.Detailed(answer)
    }

    fun goBack() {
        _uiState.value = ResultsUiState.Success(answerViewModel.answersByUser.value!!)
    }
    // Implement other logic and functions as needed
}
