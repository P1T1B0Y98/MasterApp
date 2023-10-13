package com.example.masterapp.presentation.screen.results

import com.example.masterapp.data.FHIRQuestionnaireResponseItem
import com.example.masterapp.type.QuestionnaireTypeEnum
import kotlinx.serialization.Serializable

@Serializable
data class AssessmentResponses(
    val id: String,
    val questionnaire: QuestionnaireInfo,
    val item: List<FHIRQuestionnaireResponseItem>,
    val authored: String
)

@Serializable
data class QuestionnaireInfo(
    val id: String,
    val title: String,
    val questionnaireType: QuestionnaireTypeEnum?
)



