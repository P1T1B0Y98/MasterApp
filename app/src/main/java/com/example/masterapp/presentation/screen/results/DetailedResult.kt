package com.example.masterapp.presentation.screen.results

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.masterapp.data.AnswerData
import com.example.masterapp.presentation.component.VisualizeExerciseData
import com.example.masterapp.presentation.component.VisualizeSleepData
import com.example.masterapp.presentation.component.VisualizeStressData
import androidx.compose.runtime.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.masterapp.presentation.component.GeneralInformationCard
import com.example.masterapp.presentation.theme.lightSleepColor

@Composable
fun DetailedResult(
    answer: AssessmentResponses,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 50.dp) // Add extra space at the bottom
    ) {


        item {
            GeneralInformationCard(answer = answer)    
            Spacer(Modifier.height(10.dp))
        }

        items(answer.item) { questionAnswer ->
            when (questionAnswer.type) {
                "wearable" -> {
                    when (val answerData = questionAnswer.answer) {
                        is AnswerData.FHIROBSERVATION -> {

                            when (val valueData = answerData.value.value.data) {
                                is AnswerData.Exercise -> {
                                    VisualizeExerciseData(valueData.value)
                                }
                                is AnswerData.Sleep -> {
                                    VisualizeSleepData(valueData.value)
                                }
                                is AnswerData.Stress -> {
                                    VisualizeStressData(valueData.value)
                                }
                                else -> {
                                    // Handle other cases or provide a default behavior here
                                }
                            }
}

                        else -> {
                            // Handle other cases or provide a default behavior here
                        }
                    }
                }
            }
        }

        item {
            QuestionAnswersCard(answer)
        }
    }
}

@Composable
fun QuestionAnswersCard(answer: AssessmentResponses) {
    Spacer(modifier = Modifier.height(10.dp))
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.primary)
        ) {
            Row(

            ) {
                Text(
                    text = "Questionnaire answers",
                    style = MaterialTheme.typography.h6,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp),
                )

                Icon(
                    imageVector = Icons.Rounded.QuestionAnswer,
                    contentDescription = "Information",
                    tint = Color.White,
                    modifier = Modifier.padding(start = 20.dp, top = 5.dp).size(30.dp)
                )
            }
            answer.item.forEachIndexed(){ index, questionAnswer ->
                QuestionWithDropdown(
                    questionNumber = index + 1,
                    question = questionAnswer.text,
                    answer = questionAnswer.answer
                )
                Spacer(Modifier.height(5.dp))
            }
        }
    }
}

@Composable
fun QuestionWithDropdown(questionNumber: Int, question: String, answer: AnswerData) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = lightSleepColor,
                shape = RoundedCornerShape(12.dp)),
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
                        when (answer) {
                            is AnswerData.Textual -> {
                                for (text in answer.value) {
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

