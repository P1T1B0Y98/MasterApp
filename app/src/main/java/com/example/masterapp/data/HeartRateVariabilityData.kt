package com.example.masterapp.data
import java.time.ZonedDateTime

/**
 * Represents a weight record and associated data.
 */
data class HeartRateVariabilityData(
    val heartRateVariability: Double,
    val id: String,
    val time: ZonedDateTime,
    val sourceAppInfo: MasterAppInfo?
)