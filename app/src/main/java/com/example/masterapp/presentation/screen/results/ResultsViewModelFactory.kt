package com.example.masterapp.presentation.screen.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollographql.apollo3.ApolloClient
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.roomDatabase.AnswerViewModel
import com.example.masterapp.data.roomDatabase.QuestionnaireReminderViewModel
import com.example.masterapp.presentation.screen.SharedViewModel

class ResultsViewModelFactory(
    private val questionnaireReminderViewModel: QuestionnaireReminderViewModel,
    private val apolloClient: ApolloClient
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResultsViewModel(questionnaireReminderViewModel, apolloClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
