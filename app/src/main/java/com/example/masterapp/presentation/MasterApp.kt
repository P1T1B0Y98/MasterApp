package com.example.masterapp.presentation

import AuthManager
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.masterapp.presentation.theme.Lavender
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.masterapp.NotificationSettingsManager
import com.example.masterapp.R
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.presentation.navigation.AppNavigator
import com.example.masterapp.presentation.navigation.Drawer
import com.example.masterapp.presentation.navigation.Screen
import com.example.masterapp.presentation.theme.MasterAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MasterApp(
    healthConnectManager: HealthConnectManager,
    authManager: AuthManager,
    notificationSettingsManager: NotificationSettingsManager
) {
    MasterAppTheme {
        val systemUiController = rememberSystemUiController()
        val scaffoldState = rememberScaffoldState()
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Get the user's login status from authManager
        val userLoggedIn = authManager.isSignedIn()
        Log.i("MasterApp", "userLoggedIn: $userLoggedIn")

        SideEffect {
            systemUiController.setStatusBarColor(
                color = Lavender,
                darkIcons = false
            )
        }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween, // this arrangement allows us to place elements at the start and end of the Row
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Place the icon here but make it invisible when not needed. This is to ensure equal space is always reserved on both sides.
                            if (userLoggedIn && currentRoute != Screen.SetupScreen.route) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            scaffoldState.drawerState.open()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Menu,
                                        contentDescription = stringResource(id = R.string.menu)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(48.dp))  // Replace with the size of your icon if different
                            }

                            // Your original Text composable in the center
                            Text(
                                text = getScreenNameFromRoute(currentRoute),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f) // gives the Text composable any extra space in the Row
                            )

                            // This is a placeholder space to keep symmetry. It matches the size of the IconButton.
                            Spacer(modifier = Modifier.size(48.dp))
                        }
                    },
                    elevation = 0.dp,
                )
            },
            drawerContent = {
                if (userLoggedIn && currentRoute != Screen.SetupScreen.route) {
                    Drawer(
                        scope = scope,
                        scaffoldState = scaffoldState,
                        navController = navController,
                        authManager = authManager
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(it) { data -> Snackbar(snackbarData = data) }
            },
            bottomBar = {
                if (userLoggedIn && currentRoute != Screen.SetupScreen.route) {
                    // Add the BottomNavigation component here
                    BottomNavigation(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colors.primary
                    ) {
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text(text = "Home") },
                            selected = currentRoute == Screen.HomeScreen.route,
                            onClick = {
                                navController.navigate(Screen.HomeScreen.route)
                            }
                        )
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    Icons.Default.Assessment,
                                    contentDescription = "Questionnaires"
                                )
                            },
                            label = {
                                Text(
                                    text = "Questionnaires",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(start = 0.dp, end = 0.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis // Add ellipsis for overflow
                                )
                            },
                            selected = currentRoute == Screen.AssessmentScreen.route,
                            onClick = {
                                navController.navigate(Screen.AssessmentScreen.route)
                            }
                        )
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    Icons.Default.BarChart,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text(text = "Results") },
                            selected = currentRoute == Screen.ResultsScreen.route,
                            onClick = {
                                navController.navigate(Screen.ResultsScreen.route)
                            }
                        )
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text(text = "Settings") },
                            selected = currentRoute == Screen.SettingsScreen.route,
                            onClick = {
                                navController.navigate(Screen.SettingsScreen.route)
                            }
                        )
                        // Add more BottomNavigationItem elements for your other screens
                    }
                }
            }
        ) {
            AppNavigator(
                navController = navController,
                healthConnectManager = healthConnectManager,
                authManager = authManager,
                notificationSettingsManager = notificationSettingsManager
            )
        }
    }
}

fun getScreenNameFromRoute(route: String?): String {
    return when(route) {
        Screen.HomeScreen.route -> "Home"
        Screen.LoginScreen.route -> "Login"
        Screen.AssessmentScreen.route -> "Questionnaires"
        Screen.AnswerAssessmentScreen.route -> "Answer Questionnaire"
        Screen.SettingsScreen.route -> "Settings"
        Screen.AboutScreen.route -> "About us"
        Screen.RegisterScreen.route -> "Register"
        Screen.SetupScreen.route -> "Setup"
        Screen.ResultsScreen.route -> "Results"
        Screen.ProfileScreen.route -> "Profile"
        Screen.PrivacyPolicyScreen.route -> "Privacy Policy"
        // ... add all your screens here
        else -> "Unknown"
    }
}
