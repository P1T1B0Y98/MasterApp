package com.example.masterapp.data.roomDatabase

import kotlinx.serialization.Serializable

@Serializable
data class QuestionData(
    val id: String,
    val questionType: String,
    val question: String,
    val answer: List<Map<String, List<String>>>
)

@Serializable
data class FormData(
    val questions: List<QuestionData>,
    val metadata: Map<String, String>
)
