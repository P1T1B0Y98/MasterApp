package com.example.masterapp.presentation.screen.answerassessment

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.masterapp.QUESTIONNAIRE_SUBMITMutation
import com.example.masterapp.data.AnswerData
import com.example.masterapp.data.AnswerInput
import com.example.masterapp.data.Assessment
import com.example.masterapp.data.AssessmentSchema
import com.example.masterapp.data.EncryptionHelper
import com.example.masterapp.data.ExerciseSession
import com.example.masterapp.data.FHIRMeta
import com.example.masterapp.data.FHIRQuestionnaireResponse
import com.example.masterapp.data.FHIRQuestionnaireResponseAnswer
import com.example.masterapp.data.FHIRQuestionnaireResponseItem
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.HeartRateMetrics
import com.example.masterapp.data.HeartRateVariabilityData
import com.example.masterapp.data.Item
import com.example.masterapp.data.SleepSessionData
import com.example.masterapp.data.StressData
import com.example.masterapp.data.dateTimeWithOffsetOrDefault
import com.example.masterapp.data.roomDatabase.AnswerViewModel
import com.example.masterapp.data.roomDatabase.QuestionnaireReminder
import com.example.masterapp.data.roomDatabase.QuestionnaireReminderViewModel
import com.example.masterapp.presentation.screen.SharedViewModel
import com.example.masterapp.type.QuestionnaireResponseInput
import com.example.masterapp.type.FHIRMetaInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.zip.GZIPOutputStream




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

    suspend fun collectAndSendSmartwatchData(timeInterval: String?): StressData? {
        val timeIntervalEnum = timeInterval?.let { mapDisplayValueToTimeInterval(it) }
        val startTime = timeIntervalEnum?.let { getStartTimeForInterval(it) }!!
        Log.i("StartTime", startTime.toString())
        val endTime = ZonedDateTime.now().toInstant()
        Log.i("EndTime", endTime.toString())
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

        return heartRateData?.let { StressData.HRVAndHRMetricsData(hrvData, it) }
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
                    typeOfExercise = getExerciseTypeConstantName(record.exerciseType),
                    id = record.metadata.id,
                    sourceAppInfo = healthConnectCompatibleApps[packageName],
                    sessionData = healthConnectManager.readAssociatedSessionData(record.metadata.id)
                )
            }

        return sessionsList.value
    }

    fun animateCardOffScreen(yOffset: Animatable<Float, AnimationVector1D>, onAnimationEnd: () -> Unit = {}) {
        viewModelScope.launch {
            yOffset.animateTo(targetValue = -2000f, animationSpec = tween(durationMillis = 500))
            onAnimationEnd()
        }
    }


    private fun getExerciseTypeConstantName(exerciseType: Int): String? {
        return when (exerciseType) {
            2 -> "EXERCISE_TYPE_BADMINTON"
            4 -> "EXERCISE_TYPE_BASEBALL"
            5 -> "EXERCISE_TYPE_BASKETBALL"
            8 -> "EXERCISE_TYPE_BIKING"
            9 -> "EXERCISE_TYPE_BIKING_STATIONARY"
            10 -> "EXERCISE_TYPE_BOOT_CAMP"
            11 -> "EXERCISE_TYPE_BOXING"
            13 -> "EXERCISE_TYPE_CALISTHENICS"
            14 -> "EXERCISE_TYPE_CRICKET"
            16 -> "EXERCISE_TYPE_DANCING"
            25 -> "EXERCISE_TYPE_ELLIPTICAL"
            26 -> "EXERCISE_TYPE_EXERCISE_CLASS"
            27 -> "EXERCISE_TYPE_FENCING"
            28 -> "EXERCISE_TYPE_FOOTBALL_AMERICAN"
            29 -> "EXERCISE_TYPE_FOOTBALL_AUSTRALIAN"
            31 -> "EXERCISE_TYPE_FRISBEE_DISC"
            32 -> "EXERCISE_TYPE_GOLF"
            33 -> "EXERCISE_TYPE_GUIDED_BREATHING"
            34 -> "EXERCISE_TYPE_GYMNASTICS"
            35 -> "EXERCISE_TYPE_HANDBALL"
            36 -> "EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING"
            37 -> "EXERCISE_TYPE_HIKING"
            38 -> "EXERCISE_TYPE_ICE_HOCKEY"
            39 -> "EXERCISE_TYPE_ICE_SKATING"
            44 -> "EXERCISE_TYPE_MARTIAL_ARTS"
            46 -> "EXERCISE_TYPE_PADDLING"
            47 -> "EXERCISE_TYPE_PARAGLIDING"
            48 -> "EXERCISE_TYPE_PILATES"
            50 -> "EXERCISE_TYPE_RACQUETBALL"
            51 -> "EXERCISE_TYPE_ROCK_CLIMBING"
            52 -> "EXERCISE_TYPE_ROLLER_HOCKEY"
            53 -> "EXERCISE_TYPE_ROWING"
            54 -> "EXERCISE_TYPE_ROWING_MACHINE"
            55 -> "EXERCISE_TYPE_RUGBY"
            56 -> "EXERCISE_TYPE_RUNNING"
            57 -> "EXERCISE_TYPE_RUNNING_TREADMILL"
            58 -> "EXERCISE_TYPE_SAILING"
            59 -> "EXERCISE_TYPE_SCUBA_DIVING"
            60 -> "EXERCISE_TYPE_SKATING"
            61 -> "EXERCISE_TYPE_SKIING"
            62 -> "EXERCISE_TYPE_SNOWBOARDING"
            63 -> "EXERCISE_TYPE_SNOWSHOEING"
            64 -> "EXERCISE_TYPE_SOCCER"
            65 -> "EXERCISE_TYPE_SOFTBALL"
            66 -> "EXERCISE_TYPE_SQUASH"
            68 -> "EXERCISE_TYPE_STAIR_CLIMBING"
            69 -> "EXERCISE_TYPE_STAIR_CLIMBING_MACHINE"
            70 -> "EXERCISE_TYPE_STRENGTH_TRAINING"
            71 -> "EXERCISE_TYPE_STRETCHING"
            72 -> "EXERCISE_TYPE_SURFING"
            73 -> "EXERCISE_TYPE_SWIMMING_OPEN_WATER"
            74 -> "EXERCISE_TYPE_SWIMMING_POOL"
            75 -> "EXERCISE_TYPE_TABLE_TENNIS"
            76 -> "EXERCISE_TYPE_TENNIS"
            78 -> "EXERCISE_TYPE_VOLLEYBALL"
            79 -> "EXERCISE_TYPE_WALKING"
            80 -> "EXERCISE_TYPE_WATER_POLO"
            81 -> "EXERCISE_TYPE_WEIGHTLIFTING"
            82 -> "EXERCISE_TYPE_WHEELCHAIR"
            83 -> "EXERCISE_TYPE_YOGA"
            else -> "EXERCISE_TYPE_UNKNOWN"
        }
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
        answerMap: Map<Int, AnswerData>,
        questions: List<AssessmentSchema>
    ) {

        Log.i("AnswerMap", answerMap.toString())

        val items = answerMap.map { (questionIndex, answers) ->
            val question = questions.getOrNull(questionIndex)
            val questionId = question?.linkId ?: "Question not found"
            val questionType = question?.type?.name ?: "Unknown"
            val questionText = question?.question ?: "Question not found"

            FHIRQuestionnaireResponseItem(
                linkId = questionId,
                text = questionText,
                type = questionType,
                answer = answers
            )
        }
        val questionsList = answerMap.map { (questionIndex, answers) ->
            Log.i("Answers", answers.toString())
            val question = questions.getOrNull(questionIndex)
            val questionId = question?.linkId ?: "Question not found"
            val questionType = question?.type?.name ?: "Unknown"
            val questionText = question?.question ?: "Question not found"

            AnswerInput(
                linkId = questionId,
                questionType = questionType,
                question = questionText,
                answer = answers
            )
        }

        Log.i("Question List", questionsList.toString())


        Log.i("AnswerAssessmentViewModel", "formData: $items")

        // Convert formData to a JSON string
        val serializedFormData = Json.encodeToString(items)

        Log.i("AnswerAssessmentViewModel", "formDataMap: $serializedFormData")


        // Compress the serializedFormData using GZIP
        val compressedFormData = compressStringToGZIP(serializedFormData)

        // Convert the compressed data to a base64 encoded string
        val base64CompressedFormData = android.util.Base64.encodeToString(compressedFormData, android.util.Base64.DEFAULT)

        // Encrypt the base64 encoded compressed data
        val encryptedFormData = EncryptionHelper.encrypt(base64CompressedFormData)

        // Now use enc  ryptedFormData for your mutation
        Log.i("AnswerAssessmentViewModel", "Encrypted form data: $encryptedFormData")
        val encryptedInput = QuestionnaireResponseInput(
            resourceType = "QuestionnaireResponse",
            questionnaire = assessmentId,
            status = "completed",
            subject = userId,
            authored = timestamp.toString(),
            item = encryptedFormData,
            meta = FHIRMetaInput(
                versionId = 1.toString(),
                lastUpdated = timestamp.toString(),
                source = "Android"

            )

        )


        Log.i("SerializedFormData", serializedFormData)
        Log.i("AnswerAssessmentViewModel", "Input data encrypted: $encryptedInput")

        viewModelScope.launch {
            try {
                val response =
                    apolloClient.mutate(QUESTIONNAIRE_SUBMITMutation(data = encryptedInput)).execute()
                Log.i("GraphQL", "Response: $response")
                if (response.hasErrors()) {
                    // Handle GraphQL errors
                    val errors = response.errors?.joinToString(", ") { it.message }
                    Log.e("GraphQL", "Errors: $errors")
                } else {
                    Log.i("GraphQL", frequency.toString())


                    val notificationTimestamp = frequency?.let { getFrequencyForInterval(it, timestamp) } !!
                    Log.i("Noti Timestamp", notificationTimestamp.toString())
                    val timestampUtc = timestamp.withZoneSameInstant(ZoneOffset.UTC).toInstant()
                    Log.i("Timestamp", timestampUtc.toString())
                    val reminders = questionnaireReminderViewModel.getQuestionnaireReminder(assessmentId, userId)
                    val reminder = QuestionnaireReminder(
                        id = assessmentId.hashCode(),
                        userId = userId,
                        questionnaireId = assessmentId,
                        notificationTimestamp = notificationTimestamp.toEpochMilli(),
                        completedTimestamp = timestampUtc.toEpochMilli(),
                    )
                    if (reminders != null) {
                        questionnaireReminderViewModel.updateQuestionnaireReminder(reminder)
                    } else {
                        questionnaireReminderViewModel.insertReminder(reminder)
                    }
                    // Handle successful response
                    questionnaireReminderViewModel.insertReminder(
                        QuestionnaireReminder(
                            userId = userId,
                            questionnaireId = assessmentId,
                            notificationTimestamp = notificationTimestamp.toEpochMilli(),
                            completedTimestamp = timestampUtc.toEpochMilli(),
                        )
                    )
                    // Update UI state to submitted
                    uiState.value = AnswerAssessmentUiState.Submitted
                }
            } catch (e: ApolloException) {
                // Handle the exception
                Log.e("GraphQL", "Failed to submit data", e)
                Log.e("GraphQL", "Failed to submit data", e)
            }
        }
        uiState.value = AnswerAssessmentUiState.Submitted
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

    private fun getFrequencyForInterval(interval: String, completed: ZonedDateTime): Instant {
        // Convert completed timestamp to UTC
        val completedUtc = completed.withZoneSameInstant(ZoneOffset.UTC)

        return when (interval) {
            "null" -> completedUtc.toInstant()
            "daily" -> completedUtc.plus(1, ChronoUnit.DAYS).toInstant()
            "weekly" -> completedUtc.plus(1, ChronoUnit.WEEKS).toInstant()
            "monthly" -> completedUtc.plus(1, ChronoUnit.MONTHS).toInstant()
            else -> completedUtc.toInstant()
        }
    }



}

fun compressStringToGZIP(data: String): ByteArray {
    val bos = ByteArrayOutputStream(data.length)
    GZIPOutputStream(bos).bufferedWriter(Charsets.UTF_8).use { it.write(data) }
    return bos.toByteArray()
}

enum class TimeInterval(val displayValue: String) {
    FIVE_MINUTES("5 min"),
    THIRTY_MINUTES("30 min"),
    ONE_HOUR("1 hour"),
    ONE_DAY("day"),  // updated this entry to use "day"
    ONE_WEEK("week"),
    ONE_MONTH("month")
}

