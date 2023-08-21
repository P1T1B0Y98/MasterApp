package com.example.masterapp.data

sealed class AnswerData {

    data class Textual(val values: List<String>) : AnswerData()

    data class Stress(val values: List<Any>) : AnswerData()
    data class Sleep(val data: List<SleepSessionData>) : AnswerData()
    data class Exercise(val data: List<ExerciseSession>) : AnswerData()
}

data class DynamicAnswerData(
    val type: String,
    val value: Any
)