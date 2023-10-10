package com.example.masterapp.presentation.screen.answerassessment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterapp.R
import com.example.masterapp.data.AnswerData
import com.example.masterapp.data.Assessment
import com.example.masterapp.data.AssessmentSchema
import com.example.masterapp.presentation.screen.SharedViewModel
import com.example.masterapp.presentation.theme.lightSleepColor
import com.example.masterapp.type.QuestionEnum
import kotlinx.coroutines.delay
import java.time.ZonedDateTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnswerAssessmentContent(
    viewModel: AnswerAssessmentViewModel,
    sharedViewModel: SharedViewModel,
    assessment: Assessment) {

    var currentQuestionIndex by remember { mutableStateOf(0) }
    val questions = assessment?.assessmentSchema ?: emptyList()
    val answerMap = remember { mutableStateMapOf<Int, AnswerData>() }
    var isAnswered by remember { mutableStateOf(true) }
    val isLastQuestion = currentQuestionIndex == questions.size - 1
    var isDataCollected by remember { mutableStateOf(false) }

    val smartwatchQuestionsIndices = questions.mapIndexedNotNull { index, question ->
        if (question.type == QuestionEnum.smartwatch_data) index else null
    }

    val keyboardController = LocalSoftwareKeyboardController.current


    val smartwatchDataCollected by viewModel.smartwatchDataCollected.collectAsState()
    val currentQuestion = questions[currentQuestionIndex]

    var collectedSmartwatchData: List<String> by remember { mutableStateOf(emptyList()) }

    if (smartwatchQuestionsIndices.contains(currentQuestionIndex) && !smartwatchDataCollected) {
        LaunchedEffect(currentQuestionIndex) {
            Log.i("Hello", "Collecting smartwatch data")
            Log.i("CurrentQuestion", currentQuestion.toString())
            Log.i("CurrentQuestionIndex", currentQuestionIndex.toString())
            val dataOption = currentQuestion.options?.find { it.label == "dataOption" }?.value
            val timeInterval =
                currentQuestion.options?.find { it.label == "timeIntervalOption" }?.value

            when (dataOption) {
                "Stress" -> {
                    val data = viewModel.collectAndSendSmartwatchData(timeInterval)
                    answerMap[currentQuestionIndex] =
                        data?.let { AnswerData.Stress(it, "application/json") }!!
                }

                "Sleep" -> {
                    val data = viewModel.readSleepData(timeInterval)
                    answerMap[currentQuestionIndex] =
                        data?.let { AnswerData.Sleep(it, "application/json") }!!
                }

                "Exercise" -> {
                    val data = viewModel.readExerciseSessionsData(timeInterval)
                    answerMap[currentQuestionIndex] =
                        data?.let { AnswerData.Exercise(it, "application/json") }!!
                }
            }

            for ((questionIndex, answerData) in answerMap) {
                Log.i("AnswerMap", "Question Index: $questionIndex, Answer: $answerData")
            }
            isDataCollected = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    keyboardController?.hide()
                }
            )
        }
    ) {
        Column(
 // Add top padding here
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = assessment.title,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        // Increase the size of the card to take up most of the screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f) // 80% of the screen height
        ) {

            if (!isLastQuestion) {
                Card(
                    modifier = Modifier
                        .fillMaxSize() // 80% of the screen height
                        .padding(20.dp)
                        .offset(10.dp, 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp,
                ) {

                }
            }
            Card(
                modifier = Modifier
                    .fillMaxSize() // 80% of the screen height
                    .padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentQuestionIndex < questions.size) {
                        val currentQuestion = questions[currentQuestionIndex]

                        // Display the current question index and total number of questions
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Text(
                                text = "0${currentQuestionIndex + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(start = 16.dp)
                            )

                            Text(
                                text = " of 0${questions.size}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(63.dp)
                                .height(10.dp)
                                .padding(start = 16.dp)
                                .align(Alignment.Start)
                                .background(
                                    color = MaterialTheme.colors.primary,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        )

                        if (currentQuestion != null) {
                            // Display the current question
                            Text(
                                text = currentQuestion.question ?: "",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Left,
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            val placeholderText = "Type your answer here..."
                            // Check the type of the question and render the appropriate input UI
                            when (currentQuestion.type) {
                                QuestionEnum.input, QuestionEnum.textarea, QuestionEnum.date, QuestionEnum.time, QuestionEnum.confirm -> {
                                    // Render an input field (BasicTextField) for text-based inputs
                                    val currentAnswer = answerMap[currentQuestionIndex]?.let {
                                        if (it is AnswerData.Textual) it.value.first() else ""
                                    } ?: ""
                                    BasicTextField(
                                        value = currentAnswer,
                                        onValueChange = { newValue ->
                                            answerMap[currentQuestionIndex] =
                                                AnswerData.Textual(listOf(newValue), "string" )
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
                                                keyboardController?.hide()
                                            }
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight() // Set the height of the BasicTextField to make it bigger
                                            .padding(8.dp),// Add grey border around the input field
                                        decorationBox = { innerTextField ->
                                            Box(
                                                modifier = Modifier // margin left and right
                                                    .fillMaxWidth()
                                                    .border(
                                                        width = 2.dp,
                                                        color = MaterialTheme.colors.primary,
                                                        shape = RoundedCornerShape(size = 16.dp)
                                                    )
                                                    .padding(horizontal = 16.dp, vertical = 12.dp), // inner padding
                                            ) {
                                                if (currentAnswer.isEmpty()) {
                                                    Text(
                                                        text = placeholderText,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        color = Color.LightGray,
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        }// Set the background color of the BasicTextField
                                    )
                                }

                                QuestionEnum.radio, QuestionEnum.select -> {
                                    Text(
                                        text = "Please select one option:",
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    // Render radio or select options based on the question options
                                    val selectedOptions: List<String> =
                                        when (val answer = answerMap[currentQuestionIndex]) {
                                            is AnswerData.Textual -> answer.value
                                            // If there are other AnswerData types that could be relevant here, handle them.
                                            else -> emptyList()
                                        }

                                    currentQuestion.options?.let { options ->
                                        options.forEach { option ->
                                            val isSelected = option.value in selectedOptions
                                            // Render radio buttons for the options
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                                    .padding(start = 15.dp, end = 15.dp)// Distribute equal weight to each option
                                                    .clickable {
                                                        // Since this is a radio button behavior, update the selected options to just the clicked option
                                                        val updatedOptions = listOf(option.value)
                                                        val updatedAnswerData = AnswerData.Textual(updatedOptions, "string")
                                                        answerMap[currentQuestionIndex] = updatedAnswerData
                                                    }
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f) // Equal width distribution
                                                        .background(
                                                            color = if (isSelected) lightSleepColor else Color(0xFFF2F2F2), // Change colors as needed
                                                            shape = RoundedCornerShape(16.dp)
                                                        )
                                                        .padding(16.dp)
                                                ) {
                                                    Text(
                                                        text = option.value ?: "",
                                                        color = if (isSelected) MaterialTheme.colors.primary else Color.Gray, // Change text color as needed
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal // Change font weight as needed
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }

                                QuestionEnum.checkbox -> {
                                    // Get the selected options from the answerMap
                                    val selectedOptions: List<String> =
                                        when (val answer = answerMap[currentQuestionIndex]) {
                                            is AnswerData.Textual -> answer.value
                                            else -> emptyList()
                                        }

                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = "Please select one or more options:",
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    currentQuestion.options?.let { options ->
                                        options.forEach { option ->
                                            val isSelected = option.value in selectedOptions
                                            // Render buttons for the options that look selected or deselected based on the current state
                                            Button(
                                                onClick = {
                                                    // Update the selected options based on button click
                                                    val updatedOptions = if (isSelected) {
                                                        selectedOptions - option.value
                                                    } else {
                                                        selectedOptions + option.value
                                                    }
                                                    val updatedAnswerData = AnswerData.Textual(updatedOptions, "string")
                                                    answerMap[currentQuestionIndex] = updatedAnswerData
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    backgroundColor = if (isSelected) lightSleepColor else Color(0xFFF2F2F2),
                                                    contentColor = if (isSelected) MaterialTheme.colors.primary else Color.Gray
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp)
                                                    .height(IntrinsicSize.Min)
                                            ) {
                                                Text(
                                                    text = option.value ?: "",
                                                    modifier = Modifier.padding(16.dp),
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }


                                QuestionEnum.smartwatch_data -> {
                                    // If the question is of type "smartwatch_data", skip it and move to the next question
                                    Text(
                                        text = "Please wait a moment while we collect your smartwatch data...",
                                        color = Color.Gray,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 16.dp)

                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // This will show the spinning loading icon
                                    CircularProgressIndicator(modifier = Modifier.size(40.dp))

                                    Spacer(modifier = Modifier.height(70.dp))
                                    Text(
                                        text = "We collect smartwatch data to get a more accurate and representable picture of your health.",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(175.dp)
                                            .padding(start = 16.dp, end = 16.dp)// Set the height of the BasicTextField to make it bigger
                                    )

                                    if (isDataCollected) {
                                        Text(
                                            text = "Please wait a moment while we collect your smartwatch data...",
                                            color = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // This will show the spinning loading icon
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))

                                        Spacer(modifier = Modifier.height(120.dp))
                                        Text(
                                            text = "We collect smartwatch data to get a more accurate and representable picture of your health.",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(175.dp) // Set the height of the BasicTextField to make it bigger
                                        )


                                        // Simulate a delay of 1 or 2 seconds (adjust as needed)
                                        LaunchedEffect(Unit) {
                                            delay(1000) // or delay(2000) for 2 seconds
                                            if (isLastQuestion) {
                                                submitAllAnswers(
                                                    sharedViewModel,
                                                    viewModel,
                                                    answerMap,
                                                    questions
                                                )
                                            } else {
                                                currentQuestionIndex++
                                                isDataCollected = false
                                            }
                                        }
                                    }
                                }

                                else -> {
                                    // Unsupported question type, handle it accordingly (e.g., show an error message)
                                }
                            }
                        }
                    }
                }
            }
        }
            if (currentQuestion.type != QuestionEnum.smartwatch_data)
            {
                Button(
                    onClick = {
                        // Log a message when the button is clicked
                        Log.i("MyTag", "Button clicked")

                        // Check if the current question is answered before moving to the next question
                        val answer = answerMap[currentQuestionIndex]
                        isAnswered = when (answer) {
                            is AnswerData.Textual -> answer.value.isNotEmpty()
                            else -> answer != null
                        }
                        if (isAnswered) {
                            // Check if the current question is the last question
                            Log.i("IsAnswered", isAnswered.toString())
                            if (isLastQuestion) {
                                submitAllAnswers(sharedViewModel, viewModel, answerMap, questions)
                                Log.i("Last question", "Last question and submit")
                            } else {
                                val answers = StringBuilder()
                                answerMap.forEach { (questionIndex, answerList) ->
                                    answers.append("Question $questionIndex: $answerList\n")
                                }
                                Log.d("TAG", "All questions answered:\n$answers")
                                // Move to the next question
                                currentQuestionIndex++
                            }
                        } else {
                            Log.i("IsAnswered", "Not answered")
                        }
                    },
                    modifier = Modifier
                        .size(120.dp, 75.dp)
                        .padding(16.dp)
                        .background(MaterialTheme.colors.primary, shape = RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text(
                        text = if (isLastQuestion) stringResource(id = R.string.submit_answers) else stringResource(id = R.string.next_question),
                        style = TextStyle(fontWeight = FontWeight.Bold),
                    )
                }
            }
            else {

            }
    }

    }

    // Submit all answers to the server

    // Automatically submit the answer when the focus is lost from the text field
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    DisposableEffect(currentQuestionIndex) {
        softwareKeyboardController?.hide()
        onDispose { }
    }
}

fun submitAllAnswers(sharedViewModel: SharedViewModel, viewModel: AnswerAssessmentViewModel, answerMap: Map<Int, AnswerData>, questions: List<AssessmentSchema>) {
    val userId = AuthManager.getUserId()
    val assessmentId = sharedViewModel.getAssessment()?.id ?: ""
    val assessmentTitle = sharedViewModel.getAssessment()?.title ?: ""
    val assessmentType =
        sharedViewModel.getAssessment()?.assessmentType?.name ?: "" // Assuming it's an enum
    val timestamp = ZonedDateTime.now()
    val answersToSave: Map<Int, AnswerData> = answerMap.mapValues { it.value }
    val frequency = sharedViewModel.getAssessment()?.frequency ?: ""
    viewModel.saveAnswersToDatabase(
        userId,
        assessmentId,
        assessmentTitle,
        assessmentType,
        timestamp,
        frequency,
        answersToSave,
        questions
    )
}