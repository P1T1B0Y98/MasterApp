package com.example.masterapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class AnswerData {

    @Serializable
    @SerialName("Textual")
    data class Textual(val values: List<String>) : AnswerData()

    @Serializable
    @SerialName("Stress")
    data class Stress(val data: StressData) : AnswerData()

    @Serializable
    @SerialName("Sleep")
    data class Sleep(val data: List<SleepSessionData>) : AnswerData()

    @Serializable
    @SerialName("Exercise")
    data class Exercise(val data: List<ExerciseSession>) : AnswerData()
}

@Serializable
data class QuestionData(
    val id: String,
    val questionType: String,
    val question: String,
    val answer: List<AnswerData>
)

@Serializable
data class Metadatas(
    val submissionDate: String,
    val device: String
)

@Serializable
data class FormData(
    val questions: List<QuestionData>,
    val metadata: Metadatas
)