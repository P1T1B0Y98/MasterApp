package com.example.masterapp.data

sealed class StressData {
    data class HRVData(val data: List<HeartRateVariabilityData>) : StressData()
    data class HRMetricsData(val data: HeartRateMetrics) : StressData()
}
