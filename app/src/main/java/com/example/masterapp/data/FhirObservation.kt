package com.example.masterapp.data

import kotlinx.serialization.Serializable

@Serializable
data class FhirObservation(
    val resourceType: String,
    val status: String,
    val code: FhirCodeableConcept,
    val subject: String,
    val effectiveDateTime: String,
    val value: FhirValueObject,
)

@Serializable
data class FhirValueObject(
    val data: AnswerData,
    val origin: String,
)

@Serializable
data class FhirCodeableConcept(
    val coding: List<FhirCoding>,
    val text: String
)

@Serializable
data class FhirCoding(
    val system: String?,
    val display: String
)