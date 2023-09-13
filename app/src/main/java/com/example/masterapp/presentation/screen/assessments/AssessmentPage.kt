package com.example.masterapp.presentation.screen.assessments

import AuthManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.masterapp.R
import com.example.masterapp.data.Assessment
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.presentation.component.NoQuestionnairesContent
import com.example.masterapp.presentation.navigation.Screen
import com.example.masterapp.presentation.screen.LoadingScreen
import com.example.masterapp.presentation.screen.SharedViewModel

@Composable
fun AssessmentPage(
    viewModel: AssessmentViewModel,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    healthConnectManager: HealthConnectManager
) {
    val tabOptions = listOf("Available", "Completed")
    var selectedTabIndex by remember { mutableIntStateOf(0) }  // Default to "Ready"
    val assessmentPair = viewModel.assessmentPair

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (viewModel.uiState) {
            UiState.Loading -> {
                LoadingScreen()
            }
            UiState.PermissionsAccepted -> {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabOptions.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f) // equally divide space for each tab
                                .height(48.dp) // height for tabs
                                .background(
                                    if (index == selectedTabIndex) Color(0xFF75B6D5) else MaterialTheme.colors.primary, // background color for active tab
                                )
                                .clickable {
                                    selectedTabIndex = index
                                },
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text = title,
                                color = if (index == selectedTabIndex) Color.White else Color.White,
                                fontWeight = if (index == selectedTabIndex) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(8.dp)
                            )
                            if (index == selectedTabIndex) { // underline for active tab
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }


                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (selectedTabIndex) {
                        0 -> {  // Available
                            val availableAssessments = assessmentPair?.first ?: listOf()
                            if (availableAssessments.isNotEmpty()) {
                                items(availableAssessments) { assessment ->
                                    AssessmentCard(
                                        assessment = assessment,
                                        onClick = {
                                            sharedViewModel.setAssessment(assessment)
                                            navController.navigate(Screen.AnswerAssessmentScreen.route)
                                        }
                                    )
                                }
                            } else {
                                item {
                                    NoQuestionnairesContent(type = "available")
                                }
                            }
                        }

                        1 -> {  // Completed
                            val completedAssessments = assessmentPair?.second ?: listOf()
                            if (completedAssessments.isEmpty()) {
                                item {
                                    NoQuestionnairesContent(type = "completed")
                                }
                            } else {
                                items(completedAssessments) { assessment ->
                                    AssessmentCard(
                                        assessment = assessment,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            UiState.RequestPermissions -> {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                    elevation = 8.dp,
                    backgroundColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "To continue",
                            fontSize = 24.sp,
                            color = MaterialTheme.colors.primary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = stringResource(R.string.permissions_description1),
                            fontSize = 18.sp,
                            color = MaterialTheme.colors.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onPermissionsLaunch(healthConnectManager.permissions) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.permissions_button_label),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            else -> {
                Text("Something went wrong")
            }
        }
    }
}
