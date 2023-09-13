package com.example.masterapp.presentation.component

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.masterapp.R
import com.example.masterapp.data.AnswerData
import com.example.masterapp.data.ExerciseSession
import com.example.masterapp.data.ExerciseSessionData
import com.example.masterapp.presentation.formatDuration
import com.example.masterapp.presentation.formatTimeAdjusted
import com.example.masterapp.presentation.formatTimeOnly
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.pow

@Composable
fun VisualizeExerciseData(exerciseData: List<AnswerData>) {
    val allExerciseSessions = exerciseData
        .filterIsInstance<AnswerData.Exercise>()
        .flatMap { it.data }

    val allDays = allExerciseSessions.map { it.startTime.toLocalDate() }.distinct()
    val showSnackbar = remember { mutableStateOf(false) }
    val selectedSessionIndexState = remember { mutableStateOf(0) }
    val selectedSession = remember { mutableStateOf(allExerciseSessions.getOrNull(selectedSessionIndexState.value)) }
    val isContentVisible = remember { mutableStateOf(true) } // Determines if content is expanded or shrunk

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Parent Card
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = 4.dp,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Box(Modifier.fillMaxSize()) {
                // Main Content
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
                            text = "Exercise Data",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Icon(
                            imageVector = Icons.Rounded.FitnessCenter,
                            contentDescription = "Dropdown Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 20.dp, end = 8.dp)
                                .size(40.dp)
                        )
                        // Icon to toggle content visibility
                        Icon(
                            imageVector = if (isContentVisible.value) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Content",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    isContentVisible.value = !isContentVisible.value
                                }
                                .padding(start = 5.dp)
                        )
                    }

                    // Use animatedVisibility for the collapsible content
                    AnimatedVisibility(visible = isContentVisible.value) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))

                            if (allDays.size > 1) {
                                DaysSelector(allDays) { selectedDate ->
                                    selectedSession.value = allExerciseSessions.find { it.startTime.toLocalDate() == selectedDate }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // If there's a selected session, display its details
                            ExerciseSessionCard(selectedSession.value, allExerciseSessions.size)
                        }
                    }
                }

                // Info Icon placed on the top right of the Box
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

                // Display Snackbar on top of other content in the Box
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
fun ExerciseSessionCard(exerciseSession: ExerciseSession?, size: Int) {
    // You can fill in the details for the session here, for simplicity I'm just showing the UID
    val sessionData = exerciseSession?.sessionData
    if (sessionData != null) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
        ) {
            Column(
                modifier = Modifier.
                background(MaterialTheme.colors.primary)
            ) {
                Row(

                ) {
                    // Assuming your icon in the drawable folder is named 'icon_stress'
                    if (size==1) {
                        Text(
                            text = "Date: ",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = exerciseSession.startTime.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy")),
                            style = MaterialTheme.typography.body1,
                            color = Color.White,
                            fontSize = 16.sp,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = "Type: ",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    getExerciseTypeName(exerciseSession.typeOfExercise ?: "Unknown")?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.h6,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer (modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, // Distributes space around the children
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Card for bpmMax
                    InfoCard("Start Time", formatTimeAdjusted(exerciseSession.startTime.toString()))
                    InfoCard("End Time", formatTimeAdjusted(exerciseSession.endTime.toString()))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, // Distributes space around the children
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Card for bpmMax
                    InfoCard("Duration", sessionData.totalActiveTime?.let { formatDuration(it) } ?: "N/A")
                    sessionData.totalCaloriesBurned?.let { energy ->
                        val roundedEnergyInCalories = energy.inKilocalories.round(0)
                        InfoCard("Calories", "$roundedEnergyInCalories kcal")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, // Distributes space around the children
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Card for bpmMax
                    sessionData.totalDistanceKm?.let { distance ->
                        val roundedDistanceInKm = distance.inKilometers.round(1)
                        InfoCard("Distance", "$roundedDistanceInKm km")
                    }
                    InfoCard("Total Steps", sessionData.totalSteps.toString())

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    // Card for bpmMax
                    BPMCard("Max BPM", sessionData.maxHeartRate.toString())
                    BPMCard("Min BPM", sessionData.minHeartRate.toString())
                    BPMCard("Avg BPM", sessionData.avgHeartRate.toString())
                }
                // Add more session details here...
            }
        }
    }
    else {
        Text(
            text = "No exercises found on this day",
            style = MaterialTheme.typography.body1,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .width(130.dp)
            .height(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.caption,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp,
            modifier = Modifier
                .background(MaterialTheme.colors.primary)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White)
            ) {
                Text(text = value, style = MaterialTheme.typography.body1, color = MaterialTheme.colors.primary)
            }
        }
    }
}

@Composable
fun DaysSelector(sessions: List<LocalDate>, onDayClick: (LocalDate) -> Unit) {
    val firstSessionDate = sessions.minOrNull() ?: return
    val lastSessionDate = sessions.maxOrNull() ?: return
    val scrollState = rememberLazyListState()
    val selectedDate = remember { mutableStateOf(firstSessionDate) }

    val shouldScrollLeft = remember { mutableStateOf(false) }
    val shouldScrollRight = remember { mutableStateOf(false) }

    // Generate the range of dates
    val dateRange = generateSequence(firstSessionDate) { date ->
        date.plusDays(1).takeIf { it <= lastSessionDate }
    }.toList()

    Box(
        modifier = Modifier.fillMaxWidth(), // This will make the Box take the full width available
        contentAlignment = Alignment.Center // This will center its content
    ) {
        Text(
            text = "Select a day",
            style = MaterialTheme.typography.h6,
            color = Color.White
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            LazyRow(
                state = scrollState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(dateRange) { date ->
                    DayBox(date, date == selectedDate.value) {
                        selectedDate.value = date
                        onDayClick(date)
                    }
                }
            }

            if (scrollState.firstVisibleItemIndex > 0) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Scroll Left",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .zIndex(1f)
                        .padding(8.dp)
                        .offset(x = (-20).dp) // Moves the arrow to the left by 20dp
                        .clickable { shouldScrollLeft.value = true }
                )
            }

            if (scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size < dateRange.size) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Scroll Right",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .zIndex(1f)
                        .padding(8.dp)
                        .offset(x = 20.dp) // Moves the arrow to the right by 20dp
                        .clickable { shouldScrollRight.value = true }
                )
            }
        }

        LaunchedEffect(shouldScrollLeft.value) {
            if (shouldScrollLeft.value) {
                scrollState.animateScrollToItem(scrollState.firstVisibleItemIndex - 1)
                shouldScrollLeft.value = false
            }
        }

        LaunchedEffect(shouldScrollRight.value) {
            if (shouldScrollRight.value) {
                scrollState.animateScrollToItem(scrollState.firstVisibleItemIndex + 1)
                shouldScrollRight.value = false
            }
        }
    }
}


@Composable
fun DayBox(date: LocalDate, isSelected: Boolean, onClick: () -> Unit) {
    val formattedDate = date.format(DateTimeFormatter.ofPattern("d MMM"))

    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(64.dp) // setting the width
            .height(64.dp) // setting the height
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) MaterialTheme.colors.secondary else Color.White,
                shape = RoundedCornerShape(8.dp) // adding rounded corners
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = formattedDate, color = MaterialTheme.colors.primary)
    }
}

fun Double.round(decimals: Int): Double {
    val scale = 10.0.pow(decimals)
    return (this * scale).toLong() / scale
}

private fun getExerciseTypeName(exerciseType: String): String? {
    return when (exerciseType) {
        "EXERCISE_TYPE_BADMINTON" -> "Badminton"
        "EXERCISE_TYPE_BASEBALL" -> "Baseball"
        "EXERCISE_TYPE_BASKETBALL" -> "Basketball"
        "EXERCISE_TYPE_BIKING" -> "Biking"
        "EXERCISE_TYPE_BIKING_STATIONARY" -> "Biking Stationary"
        "EXERCISE_TYPE_BOOT_CAMP" -> "Boot Camp"
        "EXERCISE_TYPE_BOXING" -> "Boxing"
        "EXERCISE_TYPE_CALISTHENICS" -> "Calisthenics"
        "EXERCISE_TYPE_CRICKET" -> "Cricket"
        "EXERCISE_TYPE_DANCING" -> "Dancing"
        "EXERCISE_TYPE_ELLIPTICAL" -> "Elliptical"
        "EXERCISE_TYPE_EXERCISE_CLASS" -> "Exercise Class"
        "EXERCISE_TYPE_FENCING" -> "Fencing"
        "EXERCISE_TYPE_FOOTBALL_AMERICAN" -> "Football American"
        "EXERCISE_TYPE_FOOTBALL_AUSTRALIAN" -> "Football Australian"
        "EXERCISE_TYPE_FRISBEE_DISC" -> "Frisbee Disc"
        "EXERCISE_TYPE_GOLF" -> "Golf"
        "EXERCISE_TYPE_GUIDED_BREATHING" -> "Guided Breathing"
        "EXERCISE_TYPE_GYMNASTICS" -> "Gymnastics"
        "EXERCISE_TYPE_HANDBALL" -> "Handball"
        "EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING" -> "High Intensity Interval Training"
        "EXERCISE_TYPE_HIKING" -> "Hiking"
        "EXERCISE_TYPE_ICE_HOCKEY" -> "Ice Hockey"
        "EXERCISE_TYPE_ICE_SKATING" -> "Ice Skating"
        "EXERCISE_TYPE_MARTIAL_ARTS" -> "Martial Arts"
        "EXERCISE_TYPE_PADDLING" -> "Paddling"
        "EXERCISE_TYPE_PARAGLIDING" -> "Paragliding"
        "EXERCISE_TYPE_PILATES" -> "Pilates"
        "EXERCISE_TYPE_RACQUETBALL" -> "Racquetball"
        "EXERCISE_TYPE_ROCK_CLIMBING" -> "Rock Climbing"
        "EXERCISE_TYPE_ROLLER_HOCKEY" -> "Roller Hockey"
        "EXERCISE_TYPE_ROWING" -> "Rowing"
        "EXERCISE_TYPE_ROWING_MACHINE" -> "Rowing Machine"
        "EXERCISE_TYPE_RUGBY" -> "Rugby"
        "EXERCISE_TYPE_RUNNING" -> "Running"
        "EXERCISE_TYPE_RUNNING_TREADMILL" -> "Running Treadmill"
        "EXERCISE_TYPE_SAILING" -> "Sailing"
        "EXERCISE_TYPE_SCUBA_DIVING" -> "Scuba Diving"
        "EXERCISE_TYPE_SKATING" -> "Skating"
        "EXERCISE_TYPE_SKIING" -> "Skiing"
        "EXERCISE_TYPE_SNOWBOARDING" -> "Snowboarding"
        "EXERCISE_TYPE_SNOWSHOEING" -> "Snowshoeing"
        "EXERCISE_TYPE_SOCCER" -> "Soccer"
        "EXERCISE_TYPE_SOFTBALL" -> "Softball"
        "EXERCISE_TYPE_SQUASH" -> "Squash"
        "EXERCISE_TYPE_STAIR_CLIMBING" -> "Stair Climbing"
        "EXERCISE_TYPE_STAIR_CLIMBING_MACHINE" -> "Stair Climbing Machine"
        "EXERCISE_TYPE_STRENGTH_TRAINING" -> "Strength Training"
        "EXERCISE_TYPE_STRETCHING" -> "Stretching"
        "EXERCISE_TYPE_SURFING" -> "Surfing"
        "EXERCISE_TYPE_SWIMMING_OPEN_WATER" -> "Swimming Open Water"
        "EXERCISE_TYPE_SWIMMING_POOL" -> "Swimming Pool"
        "EXERCISE_TYPE_TABLE_TENNIS" -> "Table Tennis"
        "EXERCISE_TYPE_TENNIS" -> "Tennis"
        "EXERCISE_TYPE_VOLLEYBALL" -> "Volleyball"
        "EXERCISE_TYPE_WALKING" -> "Walking"
        "EXERCISE_TYPE_WATER_POLO" -> "Water Polo"
        "EXERCISE_TYPE_WEIGHTLIFTING" -> "Weightlifting"
        "EXERCISE_TYPE_WHEELCHAIR" -> "Wheelchair"
        "EXERCISE_TYPE_YOGA" -> "Yoga"
        else -> "Unknown"
    }
}

