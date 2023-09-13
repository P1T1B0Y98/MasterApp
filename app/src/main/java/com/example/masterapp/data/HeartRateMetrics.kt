package com.example.masterapp.data

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
data class HeartRateMetrics(
    val startTime: String,
    val endTime: String,
    val bpmMax: Long,
    val bpmMin: Long,
    val bpmAvg: Long,
    val measurementCount: Long,
    val sourceAppInfo: MasterAppInfo?
)

fun convertHeartRateMetricsToJson(metrics: HeartRateMetrics): String {
    return Gson().toJson(metrics)
}