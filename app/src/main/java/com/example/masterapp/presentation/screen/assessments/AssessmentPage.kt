package com.example.masterapp.presentation.screen.assessments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.masterapp.data.Assessment
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.presentation.navigation.Screen
import com.example.masterapp.presentation.screen.SharedViewModel

@Composable
fun AssessmentPage(
    viewModel: AssessmentViewModel,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    healthConnectManager: HealthConnectManager,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        when (viewModel.uiState) {
            UiState.PermissionsAccepted -> {

                val assessments = remember { mutableStateListOf<Assessment>() }

                // Fetch assessments and update the list when the composable is first composed
                LaunchedEffect(Unit) {
                    assessments.clear()
                    val fetchedAssessments = viewModel.getAssessmentsList(filter = null, limit = null, offset = null, orderBy = null)
                    fetchedAssessments?.let {
                        assessments.addAll(it)
                    }
                }

                val sortedAssessments = assessments.sortedBy { it.assessmentType }

                // Display assessments when permissions are accepted
                Text(
                    text = "Assessments",
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 32.dp),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF86C6E5),
                    textAlign = TextAlign.Center
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sortedAssessments) { assessment ->
                        AssessmentCard(
                            assessment = assessment,
                            onClick = {
                                sharedViewModel.setAssessment(assessment)
                                navController.navigate(Screen.AnswerAssessmentScreen.route)
                            }
                        )
                    }
                }
            }
            UiState.RequestPermissions -> {
                // Display request for permissions when permissions are not accepted
                Text(
                    text = "Request Permissions",
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 32.dp),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF86C6E5),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = { onPermissionsLaunch(healthConnectManager.permissions)
                              },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                ) {
                    Text(
                        text = "Request Permissions",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> {}
        }
    }
}



