package com.example.masterapp.data

import android.health.connect.datatypes.SleepSessionRecord.StageType.STAGE_TYPE_AWAKE
import android.health.connect.datatypes.SleepSessionRecord.StageType.STAGE_TYPE_AWAKE_IN_BED
import android.health.connect.datatypes.SleepSessionRecord.StageType.STAGE_TYPE_SLEEPING
import android.health.connect.datatypes.SleepSessionRecord.StageType.STAGE_TYPE_UNKNOWN
import androidx.health.connect.client.records.SleepStageRecord.Companion.STAGE_TYPE_DEEP
import androidx.health.connect.client.records.SleepStageRecord.Companion.STAGE_TYPE_LIGHT
import androidx.health.connect.client.records.SleepStageRecord.Companion.STAGE_TYPE_OUT_OF_BED
import androidx.health.connect.client.records.SleepStageRecord.Companion.STAGE_TYPE_REM
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant

@Serializable
data class SleepStageData(
    val stage: String,
    @Serializable(with = InstantSerializer::class) val startTime: Instant,
    @Serializable(with = InstantSerializer::class) val endTime: Instant,
    @Serializable(with = DurationSerializer::class) val duration: Duration,
    val metadata: String
)

fun getStageConstantName(stage: Int): String {
    return when (stage) {
        STAGE_TYPE_AWAKE -> "STAGE_TYPE_AWAKE"
        STAGE_TYPE_AWAKE_IN_BED -> "STAGE_TYPE_AWAKE_IN_BED"
        STAGE_TYPE_DEEP -> "STAGE_TYPE_DEEP"
        STAGE_TYPE_LIGHT -> "STAGE_TYPE_LIGHT"
        STAGE_TYPE_OUT_OF_BED -> "STAGE_TYPE_OUT_OF_BED"
        STAGE_TYPE_REM -> "STAGE_TYPE_REM"
        STAGE_TYPE_SLEEPING -> "STAGE_TYPE_SLEEPING"
        STAGE_TYPE_UNKNOWN -> "STAGE_TYPE_UNKNOWN"
        else -> "STAGE_TYPE_INVALID"
    }
}

fun convertConstantName(stage: String): String {
    return when (stage) {
        "STAGE_TYPE_AWAKE" -> "Awake"
        "STAGE_TYPE_AWAKE_IN_BED" -> "Awake in bed"
        "STAGE_TYPE_DEEP" -> "Deep"
        "STAGE_TYPE_LIGHT" -> "Light"
        "STAGE_TYPE_OUT_OF_BED" -> "Out of bed"
        "STAGE_TYPE_REM" -> "REM"
        "STAGE_TYPE_SLEEPING" -> "Sleeping"
        "STAGE_TYPE_UNKNOWN" -> "Unknown"
        else -> "Invalid"
    }
}

@Serializable
data class SleepStagePercentage(
    val stage: String,
    val percentage: Double,
    val hoursAndMin: String,
)

fun durationToHoursMinutes(durationMillis: Long): String {
    val hours = Duration.ofMillis(durationMillis).toHours()
    val minutes = Duration.ofMillis(durationMillis).toMinutes() - hours * 60
    return "${hours}h ${minutes}m"
}

fun calculateSleepStagePercentages(sleepDataList: List<SleepStageData>): List<SleepStagePercentage> {
    // 1. Group by stage type
    val groupedStages = sleepDataList.groupBy { it.stage }

    // 2. Calculate total duration for each stage type
    val stageDurations = groupedStages.mapValues {
        it.value.sumOf { stage -> stage.duration.toMillis() }
    }

    // Calculate total sleep duration for the night
    val totalSleepDuration = sleepDataList.sumOf { it.duration.toMillis() }

    // 3. Calculate the percentage and duration for each stage type
    return stageDurations.map { (stage, duration) ->
        val stagePercentage = (duration.toDouble() / totalSleepDuration) * 100
        SleepStagePercentage(
            convertConstantName(stage),
            stagePercentage,
            durationToHoursMinutes(duration),
        )
    }
}


