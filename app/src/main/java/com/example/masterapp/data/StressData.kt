package com.example.masterapp.data

import kotlinx.serialization.Serializable

@Serializable
sealed class StressData {
    @Serializable
    data class HRVData(val data: List<HeartRateVariabilityData>) : StressData()

    @Serializable
    data class HRMetricsData(val data: HeartRateMetrics) : StressData()

    @Serializable
    data class HRVAndHRMetricsData(val hrv: List<HeartRateVariabilityData>, val hr: HeartRateMetrics) : StressData()
}
