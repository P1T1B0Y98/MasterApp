package com.example.masterapp.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollographql.apollo3.ApolloClient
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.roomDatabase.QuestionnaireReminderViewModel

class SettingsScreenViewModelFactory(
    private val healthConnectManager: HealthConnectManager,
    private val apolloClient: ApolloClient,
    private val questionnaireReminderViewModel: QuestionnaireReminderViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsScreenViewModel(
                healthConnectManager,
                apolloClient,
                questionnaireReminderViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}