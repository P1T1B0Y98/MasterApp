package com.example.masterapp.presentation.component

// on below line we are creating data class for
// pie chart data and passing variable as browser
// name and value.
data class ChartData(
    val label: String,
    val value: Float,
    val hoursAndMin: String
)
