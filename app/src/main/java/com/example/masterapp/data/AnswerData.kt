package com.example.masterapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
sealed class AnswerData {


    @Serializable
    @SerialName("input")
    data class Textual(
        val value: List<String>,
        val valueType: String?
    ) : AnswerData()

    @Serializable
    @SerialName("Stress")
    data class Stress(
        val value: StressData,
    ) : AnswerData()

    @Serializable
    @SerialName("Sleep")
    data class Sleep(
        val value: List<SleepSessionData>,
    ) : AnswerData()

    @Serializable
    @SerialName("Exercise")
    data class Exercise(
        val value: List<ExerciseSession>,
        ) : AnswerData()

    @Serializable
    @SerialName("FhirObservation")
    data class FHIROBSERVATION(
        val value: FhirObservation
    ) : AnswerData()
}

@Serializable
data class FHIRQuestionnaireResponse(
    val resourceType: String,
    val questionnaireId: String,
    val status: String,
    val subject: String,
    val authored: String,
    val item: List<FHIRQuestionnaireResponseItem>,
    val meta: FHIRMeta
)

@Serializable
data class FHIRQuestionnaireResponseItem(
    val linkId: String,
    val text: String,
    val type: String,
    val answer: AnswerData
)

@Serializable
data class FHIRQuestionnaireResponseAnswer(
    val valueString: JsonElement? = null,
    val valueType: String? = null,
    val collectionType: String? = null,
)
@Serializable
data class FHIRMeta(
    val versionId: String? = null,
    val lastUpdated: String? = null,
    val source: String? = null
)
