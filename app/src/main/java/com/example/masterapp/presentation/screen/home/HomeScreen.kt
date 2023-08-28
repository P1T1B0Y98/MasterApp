package com.example.masterapp.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.masterapp.R
import com.example.masterapp.presentation.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer (modifier = Modifier.height(25.dp))

        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground_dup),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )

        Spacer (modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to MindSync, ${AuthManager.getUserName()}. A new and innovative way of answering questionnaires.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.AssessmentScreen.route)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Questionnaires",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_question_answer_24),
                        contentDescription = "Assessment"
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.ResultsScreen.route)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Results",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 20.sp,
                        ))
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_query_stats_24),
                        contentDescription = "Results"
                    )
                }
            }
        }
    }
}




