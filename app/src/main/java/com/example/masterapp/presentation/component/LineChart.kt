import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.masterapp.data.HeartRateVariabilityData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Composable
fun HRVLineChart(hrvData: List<HeartRateVariabilityData>) {
    // Group HRV data by day and calculate daily averages
    val dailyAverages = hrvData.groupBy { hrvRecord ->
        hrvRecord.time.toLocalDate()
    }.mapValues { (_, records) ->
        records.map { it.heartRateVariability }.average()
    }

    // Convert daily averages to chart entries
    val entries = dailyAverages.entries.mapIndexed { index, entry ->
        Entry(index.toFloat(), entry.value.toFloat())
    }

    val myColor = Color.Blue // Replace with your desired color code
    val colorAsInt: Int = myColor.toArgb()

    val dataSet = LineDataSet(entries, "Daily HRV Averages")
    dataSet.color = colorAsInt
    dataSet.lineWidth = 2f
    dataSet.circleRadius = 4f

    val lineData = LineData(dataSet)
    val lineChart = LineChart(LocalContext.current)
    lineChart.apply {
        setContentDescription("HRV Line Chart")
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            400
        )
        data = lineData
        setTouchEnabled(true)
        isDragEnabled = true
        setPinchZoom(true)
        description = Description().apply {
            text = "HRV Data Over Time"
        }
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    // Display only the first and last date
                    return when (value) {
                        0f -> hrvData.firstOrNull()?.time?.let {
                            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date.from(it))
                        } ?: ""
                        (entries.size - 1).toFloat() -> hrvData.lastOrNull()?.time?.let {
                            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date.from(it))
                        } ?: ""
                        else -> ""
                    }
                }
            }
        }
        axisRight.isEnabled = false
        axisLeft.setDrawGridLines(false)
    }

    AndroidView({ lineChart }, modifier = Modifier.fillMaxSize())

}


fun Instant.toLocalDate(): LocalDate {
    return this.atZone(ZoneId.systemDefault()).toLocalDate()
}
