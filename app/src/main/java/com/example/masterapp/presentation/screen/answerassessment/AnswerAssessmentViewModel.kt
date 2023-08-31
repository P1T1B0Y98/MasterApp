package com.example.masterapp.presentation.screen.answerassessment

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.example.masterapp.ASSESSMENTS_SUBMITMutation
import com.example.masterapp.data.AnswerData
import com.example.masterapp.data.Assessment
import com.example.masterapp.data.AssessmentSchema
import com.example.masterapp.data.DynamicAnswerData
import com.example.masterapp.data.EncryptionHelper
import com.example.masterapp.data.EncryptionHelper.getSecretKey
import com.example.masterapp.data.ExerciseSession
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.HeartRateMetrics
import com.example.masterapp.data.HeartRateVariabilityData
import com.example.masterapp.data.SleepSessionData
import com.example.masterapp.data.StressData
import com.example.masterapp.data.dateTimeWithOffsetOrDefault
import com.example.masterapp.data.roomDatabase.Answer
import com.example.masterapp.data.roomDatabase.AnswerViewModel
import com.example.masterapp.data.roomDatabase.QuestionAnswer
import com.example.masterapp.data.roomDatabase.QuestionnaireReminder
import com.example.masterapp.data.roomDatabase.QuestionnaireReminderViewModel
import com.example.masterapp.presentation.screen.SharedViewModel
import com.example.masterapp.type.AssessmentResponseInput
import com.example.masterapp.type.QuestionEnum
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class AnswerAssessmentViewModel(
    private val healthConnectManager: HealthConnectManager,
    private val answerViewModel: AnswerViewModel,
    private val sharedViewModel: SharedViewModel,
    private val questionnaireReminderViewModel: QuestionnaireReminderViewModel,
    private val apolloClient: ApolloClient
) : ViewModel() {

    private val _smartwatchDataCollected = MutableStateFlow(false)
    val smartwatchDataCollected: StateFlow<Boolean> = _smartwatchDataCollected
    var sessionsList: MutableState<List<ExerciseSession>> = mutableStateOf(listOf())
        private set
    val questionsList = mutableListOf<Map<String, Any>>()
    sealed class AnswerAssessmentUiState {
        object Loading : AnswerAssessmentUiState()
        data class Success(val assessment: Assessment) : AnswerAssessmentUiState()
        object Error : AnswerAssessmentUiState()
        object Submitted : AnswerAssessmentUiState()
    }

    var uiState = mutableStateOf<AnswerAssessmentUiState>(AnswerAssessmentUiState.Loading)

    init {
        getAssessment()
    }
    // Add any necessary logic and data here to handle the assessment answers
    private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    private val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
    )

    private var permissionsGranted = healthConnectManager.isPermissionGranted

    private fun getAssessment() {
        uiState.value = AnswerAssessmentUiState.Loading

        val assessment = sharedViewModel.getAssessment()

        if (assessment != null) {
            uiState.value = AnswerAssessmentUiState.Success(assessment)
        } else {
            uiState.value = AnswerAssessmentUiState.Error
        }
    }

    fun addAnswer(questionIndex: Int, question: String, answer: Any) {
        val updatedQuestion = questionsList.getOrNull(questionIndex)?.toMutableMap()

        if (updatedQuestion != null) {
            // Update the existing question entry with the new answer
            updatedQuestion["answer"] = answer
            questionsList[questionIndex] = updatedQuestion
        } else {
            // Add a new question entry with the provided question and answer
            val newQuestionEntry = mapOf("id" to "q$questionIndex", "question" to question, "answer" to answer)
            questionsList.add(newQuestionEntry)
        }
        Log.i("AnswerAssessmentViewModel", "Questions list: $questionsList")
    }

    suspend fun collectAndSendSmartwatchData(timeInterval: String?): List<StressData?> {
        val timeIntervalEnum = timeInterval?.let { mapDisplayValueToTimeInterval(it) }
        val startTime = timeIntervalEnum?.let { getStartTimeForInterval(it) }!!
        val endTime = ZonedDateTime.now().toInstant()
        val hrvData = readHRVData(startTime, endTime)
        val heartRateData = readHeartRateData(startTime, endTime)
        // Store data or perform necessary operations

        val stressDataList = mutableListOf<StressData>()
        hrvData?.let { stressDataList.add(StressData.HRVData(it)) }
        heartRateData?.let { stressDataList.add(StressData.HRMetricsData(it)) }

        // Update the answer map with smartwatch data
        val collectedData = listOf(hrvData, heartRateData)

        // Notify that smartwatch data collection is complete
        _smartwatchDataCollected.value = true

        // Log or send the collected data
        Log.i("HealthConnect Test", collectedData.toString())

        return stressDataList
    }


    suspend fun readSleepData(timeInterval: String?): List<SleepSessionData>? {
        Log.i("Time Interval", timeInterval.toString())
        val timeIntervalEnum = timeInterval?.let { mapDisplayValueToTimeInterval(it) }
        val startTime = timeIntervalEnum?.let { getStartTimeForInterval(it) }
        val endTime = ZonedDateTime.now().toInstant()
        return startTime?.let { healthConnectManager.readSleepRecords(it, endTime) }
    }

    suspend fun readExerciseSessionsData(timeInterval: String?): List<ExerciseSession> {

        val timeIntervalEnum = timeInterval?.let { mapDisplayValueToTimeInterval(it) }
        val startTime = timeIntervalEnum?.let { getStartTimeForInterval(it) } !!
        val endTime = ZonedDateTime.now().toInstant()


        sessionsList.value = healthConnectManager
            .readExerciseSessions(startTime, endTime)
            .map { record ->
                val packageName = record.metadata.dataOrigin.packageName
                ExerciseSession(
                    startTime = dateTimeWithOffsetOrDefault(record.startTime, record.startZoneOffset),
                    endTime = dateTimeWithOffsetOrDefault(record.endTime, record.endZoneOffset),
                    id = record.metadata.id,
                    sourceAppInfo = healthConnectCompatibleApps[packageName],
                    title = record.title,
                    sessionData = healthConnectManager.readAssociatedSessionData(record.metadata.id)
                )
            }

        return sessionsList.value
    }

    private suspend fun readHRVData(startTime: Instant, endTime: Instant): List<HeartRateVariabilityData> {
        // Read HRV data from the device
        return healthConnectManager.readHeartRateVariabilityRecord(startTime, endTime)
    }

    private suspend fun readHeartRateData(startTime: Instant, endTime: Instant): HeartRateMetrics? {
        // Read heart rate data from the device
        return healthConnectManager.readHeartRateRecord(startTime, endTime)
    }


    fun saveAnswersToDatabase(
        userId: String,
        assessmentId: String,
        assessmentTitle: String,
        assessmentType: String,
        timestamp: ZonedDateTime,
        frequency: String,
        answerMap: Map<Int, List<AnswerData>>,
        questions: List<AssessmentSchema>

    ) {
        val combinedAnswerList = answerMap.map { (questionIndex, answers) ->
            val question = questions.getOrNull(questionIndex.toInt())
            val questionId = question?.field ?: "Question not found"
            val questionType = question?.type ?: "Unknown"
            val questionText = question?.question ?: "Question not found"
            val combinedAnswer = when (val answerData = answers.firstOrNull()) {
                is AnswerData.Textual -> answerData.values.joinToString(", ")
                is AnswerData.Stress -> answerData.data.joinToString(",") { it.toString() }
                is AnswerData.Sleep -> answerData.data.joinToString(", ") { it.toString() }
                is AnswerData.Exercise -> answerData.data.joinToString(", ") { it.toString() }
                else -> {
                    Log.e("AnswerAssessmentViewModel", "Unknown answer type: $answerData")
                    ""}
            }

            mapOf(
                "questionId" to questionId,
                "questionType" to questionType,
                "questionText" to questionText,
                "answer" to combinedAnswer
            )
        }

        Log.i("AnswerAssessmentViewModel", "Combined answer list: $combinedAnswerList")

// Now combinedAnswerList holds a list of maps, each containing the desired information
        combinedAnswerList.forEach { answerInfo ->
            val questionId = answerInfo["questionId"]
            val questionType = answerInfo["questionType"]
            val questionText = answerInfo["questionText"]
            val combinedAnswer = answerInfo["answer"]

            // Send the questionId, questionType, questionText, and combinedAnswer as needed
            println("Question ID: $questionId, Type: $questionType, Question: $questionText, Answer: $combinedAnswer")
        }


        val questionAnswers = answerMap.map { (questionIndex, answers) ->
            val dynamicAnswers = answers.map { answerData ->
                when (answerData) {
                    is AnswerData.Textual -> DynamicAnswerData("Textual", answerData.values.joinToString(", "))
                    is AnswerData.Stress -> DynamicAnswerData("Stress", answerData.data)
                    is AnswerData.Sleep -> DynamicAnswerData("Sleep", answerData.data)
                    is AnswerData.Exercise -> DynamicAnswerData("Exercise", answerData.data)

                    else -> null
                }
            }.filterNotNull()

            Log.i("AnswerAssessmentViewModel", "Dynamic answers for question $questionIndex: $dynamicAnswers")

            QuestionAnswer(
                questionId = questionIndex,
                answers = dynamicAnswers
            )
        }


        val answer = Answer(
            userId = userId,
            assessmentId = assessmentId,
            assessmentTitle = assessmentTitle,
            assessmentType = assessmentType,
            timestamp = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            questionAnswers = questionAnswers
        )
        Log.i("AnswerAssessmentViewModel", "Adding answer to database: $answer")
        // Assuming you have a repository or database instance, save the answer
        // Replace 'yourRepository' with your actual database or repository instance
        answerViewModel.insertAnswer(answer)

        val futureNotification = getTimeForNotification(frequency, timestamp.toInstant().toEpochMilli())
        val questionnaireReminder = QuestionnaireReminder(
            userId = userId,
            questionnaireId = assessmentId,
            completedTimestamp = timestamp.toInstant().toEpochMilli(),
            notificationTimestamp = futureNotification
        )

        questionnaireReminderViewModel.insertReminder(questionnaireReminder)

        // Convert combinedAnswerList to a list of questions, each represented as a map
        val questionsList = combinedAnswerList.map { answerInfo ->
            mapOf(
                "id" to answerInfo["questionId"],
                "questionType" to (answerInfo["questionType"] as QuestionEnum).name, // Convert enum to string
                "question" to answerInfo["questionText"],
                "answer" to answerInfo["answer"]
            )
        }

        Log.i("AnswerAssessmentViewModel", "Questions list: $questionsList")

        val questionsList1 = listOf(
            mapOf(
                "id" to "question1",
                "questionType" to "input",
                "question" to "How do you feel today?",
                "answer" to "Good"
            )
        )

        Log.i("AnswerAssessmentViewModel", "Questions list: $questionsList1")
        val metadataMap = mapOf(
            "submissionDate" to "2023-08-29",
            "device" to "Android"
        )

        val formDataMap = mapOf(
            "questions" to questionsList,
            "metadata" to metadataMap
        )

        val inputData = AssessmentResponseInput(
            assessmentID = Optional.Present(assessmentId),
            formData = Optional.Present(formDataMap)
        )

        val formDatas = inputData.formData
        Log.i("Input data", "Input data: $formDatas")

// Assuming inputData.formData is a String. If not, convert it to String before encrypting.
        val serializedFormData = Gson().toJson(formDataMap)
        val encryptedFormData = EncryptionHelper.encrypt(serializedFormData, "wilshere")

        Log.i("SerializedFormData", serializedFormData)
// Now use encryptedFormData for your mutation.
        val encryptedInput = AssessmentResponseInput(
            assessmentID = inputData.assessmentID,
            formData = Optional.Present(encryptedFormData)
        )

        Log.i("AnswerAssessmentViewModel", "Input data: $encryptedInput")

        viewModelScope.launch {
            try {
                val response =
                    apolloClient.mutate(ASSESSMENTS_SUBMITMutation(data = encryptedInput)).execute()

                if (response.hasErrors()) {
                    // Handle GraphQL errors. This could be logging, showing a message to the user, etc.
                    val errors = response.errors?.joinToString(", ") { it.message }
                    Log.e("GraphQL", "Errors: $errors")
                } else {
                    // Optionally handle successful response if you need to extract or log data.
                    // ...
                    Log.i("GraphQL", "Success: ${response.data}")
                    // Update UI state to submitted
                    uiState.value = AnswerAssessmentUiState.Submitted
                }
            } catch (e: ApolloException) {
                // Handle the exception, which can arise from network issues, server issues, etc.
                Log.e("GraphQL", "Failed to submit data", e)
            }
        }
        uiState.value = AnswerAssessmentUiState.Submitted
    }

    private fun getTimeForNotification(frequency: String, timestamp: Long): Long {
        val time = when (frequency) {
            "Daily" -> timestamp + 86400000
            "Weekly" -> timestamp + 604800000
            "Monthly" -> timestamp + 2629800000
            else -> timestamp + 86400000
        }
        return time
    }

    private fun mapDisplayValueToTimeInterval(displayValue: String): TimeInterval? {
        return TimeInterval.values().find { it.displayValue == displayValue }
    }

    private fun getStartTimeForInterval(interval: TimeInterval): Instant {
        return when (interval) {
            TimeInterval.FIVE_MINUTES -> ZonedDateTime.now().minus(5, ChronoUnit.MINUTES).toInstant()
            TimeInterval.THIRTY_MINUTES -> ZonedDateTime.now().minus(30, ChronoUnit.MINUTES).toInstant()
            TimeInterval.ONE_HOUR -> ZonedDateTime.now().minus(1, ChronoUnit.HOURS).toInstant()
            TimeInterval.ONE_DAY -> ZonedDateTime.now().minus(1, ChronoUnit.DAYS).toInstant()
            TimeInterval.ONE_WEEK -> ZonedDateTime.now().minus(1, ChronoUnit.WEEKS).toInstant()
            TimeInterval.ONE_MONTH -> ZonedDateTime.now().minus(1, ChronoUnit.MONTHS).toInstant()
        }
    }
}

enum class TimeInterval(val displayValue: String) {
    FIVE_MINUTES("5 min"),
    THIRTY_MINUTES("30 min"),
    ONE_HOUR("1 hour"),
    ONE_DAY("day"),  // updated this entry to use "day"
    ONE_WEEK("week"),
    ONE_MONTH("month")
}

