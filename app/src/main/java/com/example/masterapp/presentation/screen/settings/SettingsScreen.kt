package com.example.masterapp.presentation.screen.settings

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.navigation.NavController
import com.example.masterapp.R
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.presentation.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    healthConnectManager: HealthConnectManager,
    revokeAllPermissions: () -> Unit,
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    viewModel: SettingsScreenViewModel,
    navController: NavController
) {
    val activity = LocalContext.current
    var permissionsRevoked by remember { mutableStateOf(false) }
    var isPermissionGranted = viewModel.isPermissionGranted

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(id = R.string.settings_subtitle),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val buttonModifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 8.dp)

        Button(
            modifier = buttonModifier,
            onClick = {
                val settingsIntent = Intent()
                settingsIntent.action = HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS
                activity.startActivity(settingsIntent)
            }
        ) {
            Text(text = stringResource(id = R.string.manage))
        }

        when (viewModel.uiState) {
            UiState.RequestPermissions -> {
                Button(
                    modifier = buttonModifier,
                    onClick = {
                        onPermissionsLaunch(healthConnectManager.permissions)
                        permissionsRevoked = false
                        viewModel.uiState = UiState.RevokePermissions
                    }
                ) {
                    Text(text = stringResource(id = R.string.request_permissions))
                }
            }
            UiState.RevokePermissions -> {
                Button(
                    modifier = buttonModifier,
                    onClick = {
                        revokeAllPermissions()
                        permissionsRevoked = true
                        viewModel.uiState = UiState.RequestPermissions
                    }
                ) {
                    Text(text = stringResource(id = R.string.disconnect))
                }
            }
        }

        Button(
            modifier = buttonModifier,
            onClick = {
                // Handle navigating to the privacy policy screen
                navController.navigate(Screen.PrivacyPolicyScreen.route)
            }
        ) {
            Text(text = stringResource(id = R.string.privacy_policy_button))
        }
        // Display a message if permissions were revoked
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
