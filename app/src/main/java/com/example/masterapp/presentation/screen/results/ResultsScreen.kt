package com.example.masterapp.presentation.screen.results

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ResultsScreen(
    viewModel: ResultsViewModel = viewModel(),
) {

    when (viewModel.uiState.value) {
        is ResultsViewModel.ResultsUiState.Loading -> {
            // Display a loading indicator while loading the assessment
            Text(text = "Loading results...", color = Color.Black)

        }
        is ResultsViewModel.ResultsUiState.Success -> {
            // Display the assessment questions
            Log.i("ResultsScreen", "Displaying results")
            val answers = (viewModel.uiState.value
                    as ResultsViewModel.ResultsUiState.Success).answers
            ResultsList(answers) { selectedAnswer ->
                viewModel.onAnswerSelected(selectedAnswer) // This function needs to be defined in your ViewModel
            }
            Log.i("ResultsScreen", "Displayed results")
        }
        is ResultsViewModel.ResultsUiState.Error -> {
            // Display an error message if the assessment failed to load
            Text(text = "Failed to load assessment", color = Color.Red)
        }
        is ResultsViewModel.ResultsUiState.Detailed -> {
            // Display a success message if the assessment answers were submitted successfully
            Text(text = "Detailed results", color = Color.Red)
        }

        else -> {
            Text(text = "Failed to load assessment", color = Color.Red)
        }
    }
}

