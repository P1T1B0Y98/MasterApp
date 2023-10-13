package com.example.masterapp.data

import androidx.health.connect.client.records.SleepSessionRecord
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

/**
 * Represents sleep data, raw, aggregated and sleep stages, for a given [SleepSessionRecord].
 */
@Serializable
data class SleepSessionData(
    val uid: String,
    @Serializable(with = InstantSerializer::class) val startTime: Instant,
    @Serializable(with = ZoneOffsetSerializer::class) val startZoneOffset: ZoneOffset?,
    @Serializable(with = InstantSerializer::class) val endTime: Instant,
    @Serializable(with = ZoneOffsetSerializer::class) val endZoneOffset: ZoneOffset?,
    @Serializable(with = DurationSerializer::class) val duration: Duration?,
    val stages: List<SleepStageData> = listOf(),
    val heartRateMetrics: HeartRateMetrics
)
