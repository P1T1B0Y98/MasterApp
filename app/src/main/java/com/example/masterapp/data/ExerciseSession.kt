package com.example.masterapp.data

import java.time.ZonedDateTime

/**
 * Represents an exercise session.
 */
data class ExerciseSession(
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val id: String,
    val title: String?,
    val sourceAppInfo: MasterAppInfo?,
    val sessionData: ExerciseSessionData
)