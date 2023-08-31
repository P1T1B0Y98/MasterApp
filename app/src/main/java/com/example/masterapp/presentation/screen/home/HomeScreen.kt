package com.example.masterapp.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.navigation.NavController
import com.example.masterapp.R
import com.example.masterapp.data.DailyTips
import com.example.masterapp.presentation.navigation.Screen
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun HomeScreen(
    navController: NavController
) {

    val dailyTip = DailyTips.getRandomTip()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground_dup),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = { navController.navigate(Screen.AssessmentScreen.route) },
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
                onClick = { navController.navigate(Screen.ResultsScreen.route) },
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
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_query_stats_24),
                        contentDescription = "Results"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Tip Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(12.dp)),
            backgroundColor = MaterialTheme.colors.secondary,
            shape = RoundedCornerShape(12.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Daily Tip",
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dailyTip,
                    style = MaterialTheme.typography.body1,
                    color = Color.White
                )
            }
        }
    }
}
