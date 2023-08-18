package com.example.masterapp.data

import android.os.Parcelable
import com.example.masterapp.type.AssessmentTypeEnum
import com.example.masterapp.type.QuestionEnum
import kotlinx.parcelize.Parcelize

//Represents
@Parcelize
data class Assessment(
    val id: String,
    val title: String,
    val assessmentType: AssessmentTypeEnum?,
    val frequency: String?,
    val assessmentSchema: List<AssessmentSchema>?,
) : Parcelable

@Parcelize
data class AssessmentSchema(
    val type: QuestionEnum?,
    val question: String?,
    val field: String?,
    val options: List<Option>?,
) : Parcelable

@Parcelize
data class Option(
    val field: String,
    val value: String,
    val label: String?
): Parcelable



