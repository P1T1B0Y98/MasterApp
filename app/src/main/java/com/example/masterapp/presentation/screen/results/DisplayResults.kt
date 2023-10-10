package com.example.masterapp.presentation.screen.results

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun ResultsList(
    answers: List<AssessmentResponses?>,
    viewModel: ResultsViewModel,
    selectedAnswer: (AssessmentResponses) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 15.dp),
            ) {
                Text(
                    text = "Your answers",
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Here you can find your answered questionnaires. Click on one to see detailed results.",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                )

            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                items(answers) { answer ->
                    ResultCard(
                        answer = answer, selectedAnswer = selectedAnswer, viewModel = viewModel)
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
        }
    }
}

@Composable
fun ResultCard(
    answer: AssessmentResponses?,
    selectedAnswer: (AssessmentResponses) -> Unit,
    viewModel: ResultsViewModel
) {
    // State for showing the delete confirmation dialog
    var showDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = 20.dp,
        backgroundColor = MaterialTheme.colors.primary,
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        if (answer != null) {
                            selectedAnswer(answer)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if (answer != null) {
                        Text(
                            text = answer.questionnaire.title,
                            style = MaterialTheme.typography.h6,
                            color = Color.White,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Completed: ${answer.completed}",
                            style = MaterialTheme.typography.body2,
                            color = Color.White,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Type: ${answer.questionnaire.questionnaireType}",
                            style = MaterialTheme.typography.body1,
                            color = Color.White,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "No responses found",
                            style = MaterialTheme.typography.body1,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Go to questionnaires to answer some",
                            style = MaterialTheme.typography.body1,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Bar chart icon (non-clickable)
                Icon(
                    imageVector = Icons.Filled.StackedBarChart,
                    contentDescription = "Graph Icon",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp) // Adjust padding as needed
                )
            }

            // Delete icon in top right corner
            IconButton(
                onClick = {
                    // Show the delete confirmation dialog
                    showDialog = true
                },
                modifier = Modifier
                    .align(Alignment.TopEnd) // Align to top right
                    .padding(4.dp)
                    .size(15.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                    tint = Color.White
                )
            }
        }
    }

    // Delete confirmation dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog if canceled
                showDialog = false
            },
            title = { Text(text = "Delete Confirmation") },
            text = { Text(text = "Are you sure you want to delete this record?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Perform the delete action
                        coroutineScope.launch {
                            try {
                                viewModel.deleteQuestionnaireResponse(answer?.id ?: "")
                            } catch (e: Exception) {
                                Log.i("ResultsViewModel", "Failed to delete questionnaire response")
                            }
                            showDialog = false
                        }
                    }
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Dismiss the dialog if canceled
                        showDialog = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}





