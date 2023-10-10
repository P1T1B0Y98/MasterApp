package com.example.masterapp.data

import com.google.gson.Gson
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
fun convertStressDataToJson(stressData: StressData): String {
    return Gson().toJson(stressData)
}


