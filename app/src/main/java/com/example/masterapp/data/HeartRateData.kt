package com.example.masterapp.data
import java.time.Instant
import java.time.ZonedDateTime

/**
 * Represents a weight record and associated data.
 */
data class HeartRateData(
    val id: String,
    val startTime: Instant,
    val endTime: Instant,
    val samples: List<HeartRateDataSample>,
    val sourceAppInfo: MasterAppInfo?
)

data class HeartRateDataSample(
    val beatsPerMinute: Long,
    val time: Instant
)