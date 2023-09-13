package com.example.masterapp.presentation.component

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.masterapp.presentation.theme.awakeColor
import com.example.masterapp.presentation.theme.blueColor
import com.example.masterapp.presentation.theme.deepSleepColor
import com.example.masterapp.presentation.theme.greenColor
import com.example.masterapp.presentation.theme.lightSleepColor
import com.example.masterapp.presentation.theme.redColor
import com.example.masterapp.presentation.theme.remColor
import com.example.masterapp.presentation.theme.yellowColor
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun PieChartComposable(data: List<ChartData>) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .height(250.dp)
            .width(250.dp),
        factory = { ctx ->
        PieChart(ctx).apply {
            this.description.isEnabled = false
            this.isDrawHoleEnabled = false
            this.legend.isEnabled = true
            this.legend.textSize = 14F
            this.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            this.legend.textColor = Color.White.toArgb()
            this.setEntryLabelColor(Color.White.toArgb())
        }
    }, update = { chart ->
        updatePieChartWithData(chart, data)
    })
}

fun updatePieChartWithData(chart: PieChart, data: List<ChartData>) {
    val entries = ArrayList<PieEntry>()

    for (item in data) {
        entries.add(PieEntry(item.value, item.label))
    }
    val colorStages = data.map { getColorForStage(it.label) }.map { it.toArgb() }
    val dataSet = PieDataSet(entries, "").apply {
        colors = colorStages
        yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        sliceSpace = 2f
        valueTextColor = Color.White.toArgb()
        valueTextSize = 10f
        valueTypeface = Typeface.DEFAULT_BOLD
        valueFormatter = HoursAndMinFormatter(data)
    }

    val pieData = PieData(dataSet)
    chart.data = pieData
    chart.invalidate()
}

class HoursAndMinFormatter(private val data: List<ChartData>) : ValueFormatter() {
    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        val matchingData = data.find { it.label == pieEntry?.label }
        return matchingData?.hoursAndMin ?: ""
    }
}

fun getColorForStage(stage: String): Color {
    return when (stage) {
        "Awake" -> awakeColor
        "Light" -> lightSleepColor
        "REM" -> remColor
        "Deep" -> deepSleepColor
        // Add more stages as necessary
        else -> Color.Gray // Default color if the stage isn't recognized
    }
}
