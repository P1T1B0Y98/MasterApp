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
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.masterapp.data.roomDatabase.Answer

@Composable
fun ResultsList(
    answers: List<AssessmentResponses?>,
    selectedAnswer: (AssessmentResponses) -> Unit
) {
    Column(
        modifier = Modifier.background(color = MaterialTheme.colors.primary).fillMaxSize(),
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
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Here you can find your answered questionnaires. Click on one to see detailed results.",
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
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
                    ResultCard(answer = answer, selectedAnswer = selectedAnswer)
                }
        }




    }
}

@Composable
fun ResultCard(
    answer: AssessmentResponses?,
    selectedAnswer: (AssessmentResponses) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = 20.dp,
        backgroundColor = Color.White,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable {
                    if (answer != null) {
                        selectedAnswer(answer)
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                if (answer != null) {
                    Text(
                        text = answer.assessmentId.title,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Completed: ${answer.formData.metadata.submissionDate}",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Type: ${answer.assessmentId.assessmentType}",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.StackedBarChart,
                contentDescription = "Graph Icon",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(48.dp)  // Adjust size as needed
            )
        }
    }
}

