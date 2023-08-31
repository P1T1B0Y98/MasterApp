package com.example.masterapp.data

data class HeartRateMetrics(
    val name: String,
    val startTime: String,
    val endTime: String,
    val bpmMax: Long,
    val bpmMin: Long,
    val bpmAvg: Long,
    val measurementCount: Long,
    val sourceAppInfo: MasterAppInfo?
)