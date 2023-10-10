package com.example.masterapp.presentation.component

import HRVLineChart
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterapp.data.AnswerData
import com.example.masterapp.data.StressData
import com.example.masterapp.R
import com.example.masterapp.presentation.formatDateAndTime

@Composable
fun VisualizeStressData(
    stressData: StressData,
) {
    val showInfoSnackbar = remember { mutableStateOf(false) }
    val isContentVisible = remember { mutableStateOf(true) } // Determines if content is expanded or shrunk

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stress Data",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )

                    val icon = painterResource(id = R.drawable.baseline_monitor_heart_24)
                    Image(
                        painter = icon,
                        contentDescription = "Icon next to Stress Data title",
                        modifier = Modifier
                            .size(85.dp)
                            .padding(start = 20.dp, end = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                isContentVisible.value = !isContentVisible.value
                            }
                            .padding(start = 5.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = if (isContentVisible.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand/Collapse Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(visible = isContentVisible.value) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))

                        when (stressData) {
                            is StressData.HRVAndHRMetricsData -> {
                                val metrics = stressData.hr

                                Text("Start Time", fontWeight = FontWeight.Bold)
                                Text(formatDateAndTime(metrics.startTime))

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("End Time", fontWeight = FontWeight.Bold)
                                Text(formatDateAndTime(metrics.endTime))

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Measurement Count", fontWeight = FontWeight.Bold)
                                Text(metrics.measurementCount.toString())

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    BPMCard("Max BPM", metrics.bpmMax.toString())
                                    BPMCard("Min BPM", metrics.bpmMin.toString())
                                    BPMCard("Avg BPM", metrics.bpmAvg.toString())
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                if(stressData.hrv.isNotEmpty()) {
                                    HRVLineChart(hrvData = stressData.hrv)
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {

                                        Text(
                                            text = "HRV Value: ",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "N/A",
                                            fontSize = 16.sp,
                                            color = Color.White)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Timestamp: ",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "N/A",
                                            fontSize = 16.sp,
                                            color = Color.White)
                                    }
                                }
                            }
                            else -> { /* Handle other metric data if needed */ }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    showInfoSnackbar.value = true
                }

            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Information",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(5.dp)
                        .align(Alignment.TopEnd)
                )
            }
            if (showInfoSnackbar.value) {
                InfoSnackbar {
                    showInfoSnackbar.value = false
                }
            }

        }
}





@Composable
fun BPMCard(title: String, value: String) {
    val color = when (title) {
        "Max BPM" -> Color(0xFFD32F2F)
        "Min BPM" -> Color(0xFFFFCDD2)
        "Avg BPM" -> Color(0xFFE57373)
        else -> Color(0xFF4CAF50)
    }

    val animationDuration = when (title) {
        "Max BPM" -> 700
        "Min BPM" -> 1400
        "Avg BPM" -> 1050
        else -> 1000
    }

    val scaleState = rememberInfiniteTransition(label = "")
    val scale by scaleState.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = animationDuration
                0.8f at (animationDuration / 2)
                1.0f at animationDuration
            },
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp,
        ) {
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Heart Icon for BPM",
                    colorFilter = ColorFilter.tint(color),
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scale)
                )
                Text(value, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}





