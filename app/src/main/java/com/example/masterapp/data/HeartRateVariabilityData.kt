package com.example.masterapp.data
import java.time.Instant

/**
 * Represents a weight record and associated data.
 */
data class HeartRateVariabilityData(
    val heartRateVariability: Double,
    val id: String,
    val time: Instant,
    val sourceAppInfo: MasterAppInfo?
)