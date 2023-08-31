package com.example.masterapp.data

import android.health.connect.datatypes.SleepSessionRecord.StageType.STAGE_TYPE_AWAKE
import android.health.connect.datatypes.SleepSessionRecord.StageType.STAGE_TYPE_AWAKE_IN_BED
import android.health.connect.datatypes.SleepSessionRecord.StageType.STAGE_TYPE_SLEEPING
import android.health.connect.datatypes.SleepSessionRecord.StageType.STAGE_TYPE_UNKNOWN
import androidx.health.connect.client.records.SleepStageRecord.Companion.STAGE_TYPE_DEEP
import androidx.health.connect.client.records.SleepStageRecord.Companion.STAGE_TYPE_LIGHT
import androidx.health.connect.client.records.SleepStageRecord.Companion.STAGE_TYPE_OUT_OF_BED
import androidx.health.connect.client.records.SleepStageRecord.Companion.STAGE_TYPE_REM
import androidx.health.connect.client.records.metadata.Metadata
import java.time.Duration
import java.time.Instant

data class SleepStageData(
    val stage: String,
    val startTime: Instant,
    val endTime: Instant,
    val duration: Duration,
    val metadata: Metadata
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
