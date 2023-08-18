package com.example.masterapp.presentation.screen.answerassessment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.masterapp.R
import com.example.masterapp.data.Assessment
import com.example.masterapp.presentation.screen.SharedViewModel
import com.example.masterapp.type.QuestionEnum
import kotlinx.coroutines.delay
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnswerAssessmentContent(
    viewModel: AnswerAssessmentViewModel,
    sharedViewModel: SharedViewModel,
    assessment: Assessment) {

    var currentQuestionIndex by remember { mutableStateOf(0) }
    val questions = assessment?.assessmentSchema ?: emptyList()
    val answerMap  = remember{ mutableStateMapOf<Int, List<String>>() }// Store answers in a map by question index
    var isAnswered by remember { mutableStateOf(true) }
    val isLastQuestion = currentQuestionIndex == questions.size - 1
    var isDataCollected by remember { mutableStateOf(false) }


    val smartwatchQuestionsIndices = questions.mapIndexedNotNull { index, question ->
        if (question.type == QuestionEnum.smartwatch_data) index else null
    }


    val smartwatchDataCollected by viewModel.smartwatchDataCollected.collectAsState()

    var collectedSmartwatchData: List<String> by remember { mutableStateOf(emptyList()) }

    if (smartwatchQuestionsIndices.contains(currentQuestionIndex) && !smartwatchDataCollected) {
        LaunchedEffect(currentQuestionIndex) {
            val data = viewModel.collectAndSendSmartwatchData()
            viewModel.readSleepData()
            viewModel.readExerciseSessionsData()
            collectedSmartwatchData = data
            answerMap[currentQuestionIndex] = data
            isDataCollected = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Increase the size of the card to take up most of the screen
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f) // 80% of the screen height
                .padding(16.dp),
            elevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // Allow content to scroll if needed
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (assessment != null) {
                    // Display the assessment title inside the card
                    Text(
                        text = assessment.title,
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (currentQuestionIndex < questions.size) {
                    val currentQuestion = questions[currentQuestionIndex]

                    // Display the current question index and total number of questions
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (currentQuestion != null) {
                        // Display the current question
                        Text(
                            text = currentQuestion.question ?: "",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Check the type of the question and render the appropriate input UI
                        when (currentQuestion.type) {
                            QuestionEnum.input, QuestionEnum.textarea, QuestionEnum.date, QuestionEnum.time, QuestionEnum.confirm -> {
                                // Render an input field (BasicTextField) for text-based inputs
                                val answer = answerMap[currentQuestionIndex]?.joinToString(", ") ?: ""
                                BasicTextField(
                                    value = answer,
                                    onValueChange = { newValue ->
                                        answerMap[currentQuestionIndex] = newValue.split(", ")
                                        Log.d("TAG", "Added answer for question $currentQuestionIndex: ${answerMap[currentQuestionIndex]}")
                                        Log.d("TAG", "Answer map: $answerMap")
                                    },

                                    textStyle = TextStyle(color = Color.Black),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = when (currentQuestion.type) {
                                            QuestionEnum.input -> KeyboardType.Text
                                            QuestionEnum.textarea -> KeyboardType.Text
                                            QuestionEnum.date -> KeyboardType.Text
                                            QuestionEnum.time -> KeyboardType.Text
                                            QuestionEnum.confirm -> KeyboardType.Text
                                            else -> KeyboardType.Text
                                        },
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            // Move to the next question
                                            val answer = answerMap[currentQuestionIndex]
                                            if (answer != null && answer.isNotEmpty()) {
                                                currentQuestionIndex++
                                                // Check if all questions have been answered
                                                if (currentQuestionIndex >= questions.size) {
                                                    // All questions answered, do something (e.g., submit answers)
                                                }
                                                isAnswered = true
                                            } else {
                                                isAnswered = false
                                            }
                                        }
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp) // Set the height of the BasicTextField to make it bigger
                                        .padding(8.dp)
                                        .border(
                                            1.dp,
                                            Color.Gray,
                                            MaterialTheme.shapes.small
                                        ) // Add grey border around the input field
                                        .background(Color.LightGray) // Set the background color of the BasicTextField
                                )
                            }
                            QuestionEnum.radio, QuestionEnum.select -> {
                                // Render radio or select options based on the question options
                                val selectedOptions = answerMap[currentQuestionIndex] ?: emptyList()

                                currentQuestion.options?.let { options ->
                                    options.forEach { option ->
                                        val isSelected = option.value in selectedOptions
                                        // Render checkboxes for the options
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable {
                                                // Update the selected options based on checkbox selection
                                                val updatedOptions = if (isSelected) {
                                                    selectedOptions - option.value
                                                } else {
                                                    selectedOptions + option.value
                                                }
                                                answerMap[currentQuestionIndex] = updatedOptions
                                            }
                                        ) {
                                            Checkbox(
                                                checked = isSelected,
                                                onCheckedChange = null // The `onCheckedChange` is handled in the clickable modifier above
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = option.value ?: "", color = Color.Black)
                                        }
                                    }
                                }
                            }
                            QuestionEnum.checkbox -> {
                                // Render checkboxes based on the question options
                                val selectedOptions = answerMap[currentQuestionIndex] ?: emptyList()

                                currentQuestion.options?.let { options ->
                                    options.forEach { option ->
                                        val isSelected = option.field in selectedOptions
                                        // Render checkboxes for the options
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable {
                                                // Update the selected options based on checkbox selection
                                                val updatedOptions = if (isSelected) {
                                                    selectedOptions - option.field
                                                } else {
                                                    selectedOptions + option.field
                                                }
                                                answerMap[currentQuestionIndex] = updatedOptions
                                            }
                                        ) {
                                            Checkbox(
                                                checked = isSelected,
                                                onCheckedChange = null // The `onCheckedChange` is handled in the clickable modifier above
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = option.field ?: "", color = Color.Black)
                                        }
                                    }
                                }
                            }
                            QuestionEnum.smartwatch_data -> {
                                // If the question is of type "smartwatch_data", skip it and move to the next question

                                if (isDataCollected) {
                                    Log.i("TAG", "Data collected from smartwatch")
                                    if (!collectedSmartwatchData.isNullOrEmpty()) {
                                        Text(text = "Collecting data from smartwatch...", color = Color.Gray)
                                    }
                                    // Simulate a delay of 1 or 2 seconds (adjust as needed)
                                    LaunchedEffect(Unit) {
                                        delay(2000) // or delay(2000) for 2 seconds
                                        currentQuestionIndex++
                                        isDataCollected = false
                                    }
                                }
                            }
                            else -> {
                                // Unsupported question type, handle it accordingly (e.g., show an error message)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display "Next" button to move to the next question
                        Button(
                            onClick = {
                                // Check if the current question is answered before moving to the next question
                                val answer = answerMap[currentQuestionIndex]
                                isAnswered = if (!answer.isNullOrEmpty()) {
                                    if (isLastQuestion) {
                                        // All questions answered, do something (e.g., submit answers)
                                        val userId = AuthManager.getUserId()
                                        val assessmentId = sharedViewModel.getAssessment()?.id ?: ""
                                        val assessmentTitle = sharedViewModel.getAssessment()?.title ?: ""
                                        val assessmentType = sharedViewModel.getAssessment()?.assessmentType?.name ?: "" // Assuming it's an enum
                                        val timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

                                        viewModel.saveAnswersToDatabase(
                                            userId,
                                            assessmentId,
                                            assessmentTitle,
                                            assessmentType,
                                            timestamp,
                                            answerMap
                                        )

                                        val answers = StringBuilder()
                                        answerMap.forEach { (questionIndex, answerList) ->
                                            answers.append("Question $questionIndex: $answerList\n")
                                        }
                                        Log.d("TAG", "All questions answered:\n$answers")
                                    } else {
                                        val answers = StringBuilder()
                                        answerMap.forEach { (questionIndex, answerList) ->
                                            answers.append("Question $questionIndex: $answerList\n")
                                        }
                                        Log.d("TAG", "All questions answered:\n$answers")
                                        // Move to the next question
                                        currentQuestionIndex++
                                    }
                                    true
                                } else {
                                    false
                                }
                            }
                        ) {
                            Text(text = if (isLastQuestion) stringResource(id = R.string.submit_answers) else stringResource(id = R.string.next_question))
                        }

                        // Display error message if the current question is not answered
                        if (!isAnswered) {
                            Text(
                                text = "Please answer the current question.",
                                color = Color.Red,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    // Automatically submit the answer when the focus is lost from the text field
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    DisposableEffect(currentQuestionIndex) {
        softwareKeyboardController?.hide()
        onDispose { }
    }
}
