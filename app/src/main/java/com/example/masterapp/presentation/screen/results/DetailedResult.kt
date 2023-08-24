package com.example.masterapp.presentation.screen.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.masterapp.data.roomDatabase.Answer

@Composable
fun DetailedResult(
    answer: Answer,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { onBack }) {
            Text("Back to Results")
        }
        Text(
            text = "Detailed Results for ${answer.assessmentTitle}",
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 32.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        // Placeholder for your graphics related to the answer's results.
        // This could be a bar chart, pie chart, or any other visualization.
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Gray)
        ) {
            Text(
                text = "Graphics Placeholder",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "More details and breakdowns can be added below. You might want to consider charts, lists, etc.",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
