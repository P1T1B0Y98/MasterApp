package com.example.masterapp.presentation.screen.settings

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.navigation.NavController
import com.example.masterapp.NotificationSettingsManager
import com.example.masterapp.R
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.presentation.component.MyCard
import com.example.masterapp.presentation.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    healthConnectManager: HealthConnectManager,
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    viewModel: SettingsScreenViewModel,
    navController: NavController
) {
    val activity = LocalContext.current
    var permissionsRevoked by remember { mutableStateOf(false) }
    val notificationSettingsManager = NotificationSettingsManager(activity)
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(MaterialTheme.colors.primary),
        userScrollEnabled = true,
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.settings_subtitle),
                style = MaterialTheme.typography.body2,
                color = Color.White,
                modifier = Modifier.padding(32.dp, 0.dp, 32.dp, 16.dp)
            )
        }

        item {
            MyCard(
                title = stringResource(id = R.string.manage),
                description = stringResource(id = R.string.manage_description),
                buttonText = stringResource(id = R.string.manage),
                onClick = {
                    val settingsIntent = Intent()
                    settingsIntent.action = HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS
                    activity.startActivity(settingsIntent)
                }
            )
        }

        item {
            when (viewModel.uiState) {
                UiState.RequestPermissions -> {
                    MyCard(
                        title = stringResource(id = R.string.request_permissions),
                        description = stringResource(id = R.string.request_permissions_description),
                        buttonText = stringResource(id = R.string.request_permissions),
                        onClick = {
                            onPermissionsLaunch(healthConnectManager.permissions)
                        }
                    )
                }

                UiState.RevokePermissions -> {
                    MyCard(
                        title = stringResource(id = R.string.disconnect),
                        description = stringResource(id = R.string.disconnect_description),
                        buttonText = stringResource(id = R.string.disconnect),
                        onClick = { viewModel.revokePermissions() }
                    )
                }
            }
        }

        item {
            MyCard(
                title = stringResource(id = R.string.notifications),
                description = stringResource(id = R.string.notifications_description),
                buttonText = stringResource(id = R.string.notifications),
                onClick = {
                    notificationSettingsManager.openNotificationSettings()
                }
            )
        }

        item {
            MyCard(
                title = stringResource(id = R.string.delete_all_data),
                description = stringResource(id = R.string.delete_all_data_description),
                buttonText = stringResource(id = R.string.delete_all),
                onClick = {
                    showDialog = true
                }
            )
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog if canceled
                        showDialog = false
                    },
                    title = { Text(text = "Delete Confirmation") },
                    text = { Text(text = "Are you sure you want to delete all records?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Perform the delete action
                                coroutineScope.launch {
                                    try {
                                        viewModel.deleteAllQuestionnaireResponses(AuthManager.getUserId())
                                    } catch (e: Exception) {
                                        Log.i("ResultsViewModel", "Failed to delete questionnaire response")
                                    }
                                    showDialog = false
                                }
                            }
                        ) {
                            Text(text = "Delete")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                // Dismiss the dialog if canceled
                                showDialog = false
                            }
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                )
            }
        }


        item {
            MyCard(
                title = stringResource(id = R.string.privacy_policy_button),
                description = "Click here to see our privacy policy",
                buttonText = stringResource(id = R.string.privacy_policy_button),
                onClick = {
                    navController.navigate(Screen.PrivacyPolicyScreen.route)
                }
            )
            Spacer(modifier = Modifier.height(70.dp))
        }

        item {
            if (permissionsRevoked) {
                AlertDialog(
                    onDismissRequest = {
                        permissionsRevoked = false
                    },
                    title = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.permissions_revoked_title),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.permissions_revoked_message),
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    },
                    confirmButton = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
                                    permissionsRevoked = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(text = stringResource(id = R.string.ok_button))
                            }
                        }
                    }
                )
            }
        }
    }
}