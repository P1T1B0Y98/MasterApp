package com.example.masterapp.presentation.component

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterapp.data.AnswerData
import com.example.masterapp.data.SleepSessionData
import com.example.masterapp.data.calculateSleepStagePercentages
import com.example.masterapp.presentation.formatDuration
import com.example.masterapp.presentation.formatTimeOnly
import java.time.ZoneId

@Composable
fun VisualizeSleepData(sleepData: List<SleepSessionData>) {

    val allSleepDays = sleepData.map { it.startTime.atZone(ZoneId.systemDefault()).toLocalDate() }.distinct()
    val showSnackbar = remember { mutableStateOf(false) }
    val expandedState = remember { mutableStateOf(true) } // Initial state is expanded

    val selectedDateState = remember { mutableStateOf(allSleepDays.firstOrNull()) }
    val selectedSleepSession = sleepData.find { it.startTime.atZone(ZoneId.systemDefault()).toLocalDate() == selectedDateState.value }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Parent Card
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Box(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sleep Data",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Icon(
                            imageVector = Icons.Rounded.Bedtime,
                            contentDescription = "Dropdown Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 20.dp, end = 8.dp)
                                .size(40.dp)
                        )
                        Icon(
                            imageVector = if (expandedState.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle Content",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    expandedState.value = !expandedState.value
                                }
                                .padding(start = 5.dp)
                        )
                    }

                    AnimatedVisibility(visible = expandedState.value) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            if(allSleepDays.size > 1){
                                DaysSelector(allSleepDays) { selectedDate ->
                                    selectedDateState.value = selectedDate
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            SleepSessionCard(selectedSleepSession, sleepData.size)
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Information",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .padding(4.dp)
                        .clickable {
                            showSnackbar.value = true
                        }
                )

                if (showSnackbar.value) {
                    InfoSnackbar {
                        showSnackbar.value = false
                    }
                }
            }
        }
    }
}

@Composable
fun InfoSnackbar(onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Snackbar(
            action = {
                Box(
                    modifier = Modifier.background(MaterialTheme.colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Dismiss", color = Color.White)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(20.dp),
            backgroundColor = Color.White,
            shape = RoundedCornerShape(8.dp),
            contentColor = MaterialTheme.colors.primary
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Information",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "This is a visualization of the data that was collected while you answered the questionnaire. It helps us get a better understanding of your health and gives an accurate response to our questions when applicable. This benefits both you and therapists analyzing the results."
                )
            }
        }
    }
}

@Composable
fun SleepSessionCard(
    sleepSession: SleepSessionData?,
    size: Int
) {
    if (sleepSession != null) {
        val stages = sleepSession.stages

        val stagePercentages = calculateSleepStagePercentages(stages)

        val pieChartData = stagePercentages.map {
            ChartData(it.stage, it.percentage.toFloat(), it.hoursAndMin)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.primary)

            ) {
                Text(
                    text = "Sleep Session",
                    style = MaterialTheme.typography.h6,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,

                    )

                Spacer(modifier = Modifier.height(8.dp))


                if (size==1) {
                    Text(
                        text = "Date: ",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = sleepSession.startTime.atZone(ZoneId.systemDefault()).toLocalDate().toString(),
                        style = MaterialTheme.typography.body1,
                        color = Color.White,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = "Duration: ",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = sleepSession.duration?.let { formatDuration(it) } ?: "N/A",
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    fontSize = 16.sp,
                )



                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Start Time: ",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = formatTimeOnly(sleepSession.startTime.toString()),
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    fontSize = 16.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "End Time: ",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = formatTimeOnly(sleepSession.endTime.toString()),
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    fontSize = 16.sp,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Time in each sleep stage",
                    style = MaterialTheme.typography.h6,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(8.dp))

                PieChartComposable(data = pieChartData)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Heart Rate Data",
                    style = MaterialTheme.typography.h6,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    BPMCard("Max BPM", sleepSession.heartRateMetrics.bpmMax.toString())
                    BPMCard("Min BPM", sleepSession.heartRateMetrics.bpmMin.toString())
                    BPMCard("Avg BPM", sleepSession.heartRateMetrics.bpmAvg.toString())
                }
            }
        }
    } else {
        Text(
            text = "No sleep session data available for this date",
            style = MaterialTheme.typography.h6,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(text ="Remember to use your smartwatch to track your sleep each night",
            style = MaterialTheme.typography.body1,
            color = Color.White,
            fontSize = 16.sp)
    }

}
