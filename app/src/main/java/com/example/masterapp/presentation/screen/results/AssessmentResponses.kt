package com.example.masterapp.presentation.screen.results

import android.os.Parcelable
import com.example.masterapp.data.AnswerData
import com.example.masterapp.data.FormData
import com.example.masterapp.type.AssessmentTypeEnum
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class AssessmentResponses(
    val id: String,
    val assessmentId: AssessmentInfo,
    val formData: FormData
)

@Serializable
data class AssessmentInfo(
    val id: String,
    val title: String,
    val assessmentType: AssessmentTypeEnum?
)



