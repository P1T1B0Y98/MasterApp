package com.example.masterapp.data

import android.os.Parcelable
import com.example.masterapp.type.QuestionnaireTypeEnum
import com.example.masterapp.type.QuestionEnum
import kotlinx.parcelize.Parcelize

//Represents
@Parcelize
data class Assessment(
    val id: String,
    val title: String,
    val description: String,
    val assessmentType: QuestionnaireTypeEnum?,
    val frequency: String?,
    val assessmentSchema: List<AssessmentSchema>?,
) : Parcelable

@Parcelize
data class AssessmentSchema(
    val type: QuestionEnum?,
    val linkId: String?,
    val question: String?,
    val options: List<Option>?,
) : Parcelable

@Parcelize
data class Option(
    val value: String,
    val label: String?
): Parcelable



