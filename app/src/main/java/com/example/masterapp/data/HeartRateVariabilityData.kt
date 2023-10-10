package com.example.masterapp.data
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Represents a weight record and associated data.
 */
@Serializable
data class HeartRateVariabilityData(
    val heartRateVariability: Double,
    val id: String,
    @Serializable(with = InstantSerializer::class) val time: Instant,
)