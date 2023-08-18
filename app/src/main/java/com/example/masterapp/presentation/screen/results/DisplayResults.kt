package com.example.masterapp.presentation.screen.results

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.masterapp.data.roomDatabase.Answer

@Composable
fun ResultsList(
    answers: List<Answer>,
    selectedAnswer: (Answer) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Results",
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Here you can find your answered assessments. Click on one to see detailed result",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            textAlign = TextAlign.Center,
        )

        Log.i("ResultsList", "Trying to build column")
        LazyColumn {
            items(answers) { answer ->
                ResultCard(answer, selectedAnswer)
            }
        }
    }
}

@Composable
fun ResultCard(
    answer: Answer,
    selectedAnswer: (Answer) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable { selectedAnswer(answer) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = answer.assessmentTitle,
                    style = MaterialTheme.typography.h6,
                    color = Color.White,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Completed: ${answer.timestamp}",
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Type: ${answer.assessmentType}",
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Icon(
                imageVector = Icons.Filled.StackedBarChart,
                contentDescription = "Graph Icon",
                tint = Color.White,
                modifier = Modifier.size(48.dp)  // Adjust size as needed
            )
        }
    }
}

