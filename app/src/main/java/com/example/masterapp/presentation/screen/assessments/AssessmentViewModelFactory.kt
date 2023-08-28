package com.example.masterapp.presentation.screen.assessments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollographql.apollo3.ApolloClient
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.roomDatabase.QuestionnaireRepository
import com.example.masterapp.presentation.screen.assessments.AssessmentViewModel


class AssessmentViewModelFactory(
    private val apolloClient: ApolloClient,
    private val healthConnectManager: HealthConnectManager,
    private val questionnaireRepository: QuestionnaireRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssessmentViewModel::class.java)) {
            return AssessmentViewModel(apolloClient, healthConnectManager, questionnaireRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}