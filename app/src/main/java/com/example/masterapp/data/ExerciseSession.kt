package com.example.masterapp.data

import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

/**
 * Represents an exercise session.
 */
@Serializable
data class ExerciseSession(
    @Serializable(with = ZonedDateTimeSerializer::class) val startTime: ZonedDateTime,
    @Serializable(with = ZonedDateTimeSerializer::class) val endTime: ZonedDateTime,
    val id: String,
    val typeOfExercise: String?,
    val sourceAppInfo: MasterAppInfo?,
    val sessionData: ExerciseSessionData
)
