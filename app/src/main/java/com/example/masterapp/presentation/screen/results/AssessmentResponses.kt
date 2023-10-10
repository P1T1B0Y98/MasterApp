package com.example.masterapp.presentation.screen.results

import android.os.Parcelable
import com.example.masterapp.data.AnswerData
import com.example.masterapp.data.FHIRQuestionnaireResponseItem
import com.example.masterapp.data.Item
import com.example.masterapp.type.DateTime
import com.example.masterapp.type.QuestionnaireTypeEnum
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class AssessmentResponses(
    val id: String,
    val questionnaire: QuestionnaireInfo,
    val item: List<FHIRQuestionnaireResponseItem>,
    val completed: String
)

@Serializable
data class QuestionnaireInfo(
    val id: String,
    val title: String,
    val questionnaireType: QuestionnaireTypeEnum?
)



