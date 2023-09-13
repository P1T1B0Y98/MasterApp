package com.example.masterapp.data

import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import kotlinx.serialization.Serializable
import java.time.Duration

/**
 * Represents data, both aggregated and raw, associated with a single exercise session. Used to
 * collate results from aggregate and raw reads from Health Connect in one object.
 */
@Serializable
data class ExerciseSessionData(
    val uid: String,
    @Serializable(with = DurationSerializer::class) val totalActiveTime: Duration? = null,
    val totalSteps: Long? = null,
    @Serializable(with = LengthSerializer::class) val totalDistanceKm: Length? = null,
    @Serializable(with = EnergySerializer::class) val totalCaloriesBurned: Energy? = null,
    val minHeartRate: Long? = null,
    val maxHeartRate: Long? = null,
    val avgHeartRate: Long? = null,
    @Serializable(with = CustomVelocitySerializer::class) val minSpeed: Velocity? = null,
    @Serializable(with = CustomVelocitySerializer::class) val maxSpeed: Velocity? = null,
    @Serializable(with = CustomVelocitySerializer::class) val avgSpeed: Velocity? = null,
)