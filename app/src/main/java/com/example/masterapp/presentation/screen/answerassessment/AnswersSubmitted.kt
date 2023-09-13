package com.example.masterapp.presentation.screen.answerassessment

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.masterapp.R
import kotlinx.coroutines.delay

@Composable
fun AnswersSubmitted(onGoToResults: () -> Unit, onGoToAssessments: () -> Unit) {
    var loadingComplete by remember { mutableStateOf(false) }
    var progressValue by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = Unit) {
        while (progressValue < 1f) {
            delay(100)  // this will determine the speed of loading. Adjust as needed.
            progressValue += 0.10f  // this will determine the step of increment. Adjust as needed.
        }
        loadingComplete = true
    }

    if (!loadingComplete) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colors.primary)
        ) {
            LinearProgressIndicator(
                progress = progressValue,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                color = Color.White,
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colors.primary)
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground_dup),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = "Thank you for completing the questionnaire. Be proud of yourself!",
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ){
                Button(
                    onClick = onGoToResults,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)// equally distribute space
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text(text = "Go to Results")
                }

                Button(
                    onClick = onGoToAssessments,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)// equally distribute space
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text(text = "Answer More Questionnaires", color = MaterialTheme.colors.primary)
                }
            }
        }
    }
}



