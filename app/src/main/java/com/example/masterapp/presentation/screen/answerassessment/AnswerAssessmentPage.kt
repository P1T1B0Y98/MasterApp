package com.example.masterapp.presentation.screen.answerassessment

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.masterapp.presentation.screen.SharedViewModel


@Composable
fun AnswerAssessmentPage(
    viewModel: AnswerAssessmentViewModel,
    sharedViewModel: SharedViewModel,
    onGoToResults: () -> Unit,
    onGoToAssessments: () -> Unit
) {
    Log.d("AnswerAssessmentPage", "Composable recomposed")

    when (viewModel.uiState.value) {
        is AnswerAssessmentViewModel.AnswerAssessmentUiState.Loading -> {
            // Display a loading indicator while loading the assessment
            Text(text = "Loading assessment...", color = Color.Black)
        }
        is AnswerAssessmentViewModel.AnswerAssessmentUiState.Information -> {
            // Display the assessment information
            val assessment = (viewModel.uiState.value as AnswerAssessmentViewModel.AnswerAssessmentUiState.Information).assessment
            AnswerAssessmentInformation(assessment, viewModel, onGoToAssessments)
        }
        is AnswerAssessmentViewModel.AnswerAssessmentUiState.Success -> {
            // Display the assessment questions
            val assessment = (viewModel.uiState.value as AnswerAssessmentViewModel.AnswerAssessmentUiState.Success).assessment
            AnswerAssessmentContent(viewModel, sharedViewModel, assessment)
        }
        is AnswerAssessmentViewModel.AnswerAssessmentUiState.Error -> {
            // Display an error message if the assessment failed to load
            Text(text = "Failed to load assessment", color = Color.Red)
        }
        is AnswerAssessmentViewModel.AnswerAssessmentUiState.Submitted -> {
            // Display a success message if the assessment answers were submitted successfully
            AnswersSubmitted(onGoToResults, onGoToAssessments)
                
        }

        else -> {}
    }
}


