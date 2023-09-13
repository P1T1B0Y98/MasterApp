package com.example.masterapp.presentation.screen.results

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.masterapp.data.AnswerData
import com.example.masterapp.presentation.component.VisualizeExerciseData
import com.example.masterapp.presentation.component.VisualizeSleepData
import com.example.masterapp.presentation.component.VisualizeStressData
import androidx.compose.runtime.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.DpOffset
import com.example.masterapp.presentation.component.ChartData
import com.example.masterapp.presentation.component.PieChartComposable


@Composable
fun DetailedResult(
    answer: AssessmentResponses,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(end = 16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint= MaterialTheme.colors.primary// Adjust width and height here
                    )
                }


                Text(
                    text = answer.assessmentId.title,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(bottom = 10.dp),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Completed at: ${answer.formData.metadata.submissionDate}",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(bottom = 5.dp),
                textAlign = TextAlign.Center
            )
        }

        items(answer.formData.questions) { questionAnswer ->
            when (questionAnswer.questionType) {
                "smartwatch_data" -> {
                    when (answer.assessmentId.assessmentType.toString()) {
                        "physical_health" -> VisualizeExerciseData(questionAnswer.answer)
                        "stress" -> VisualizeStressData(questionAnswer.answer)
                        "sleep" -> VisualizeSleepData(questionAnswer.answer)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(answer.formData.questions) { index, questionAnswer ->
            QuestionWithDropdown(questionNumber = index + 1, question = questionAnswer.question, answer = questionAnswer.answer)
            Spacer(Modifier.height(5.dp))
        }
    }
}

@Composable
fun QuestionWithDropdown(questionNumber: Int, question: String, answer: List<AnswerData>) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.primary)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Q$questionNumber: $question",
                    style = MaterialTheme.typography.h6,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand Card",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp)
                        .rotate(if (expanded) 180f else 0f),
                    tint = Color.White
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        for (answer in answer) {
                            when (answer) {
                                is AnswerData.Textual -> {
                                    for (text in answer.values) {
                                        Text(
                                            "Answer: $text",
                                            color = MaterialTheme.colors.primary,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                                else -> {
                                    Text(
                                        "Answer: See the answer over. This was a smartwatch data question.",
                                        color = MaterialTheme.colors.primary,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}











