package com.example.masterapp.presentation.navigation

import AuthManager
import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.Crossfade
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.masterapp.NotificationSettingsManager
import com.example.masterapp.data.HealthConnectManager
import com.example.masterapp.data.roomDatabase.AnswerViewModel
import com.example.masterapp.data.roomDatabase.AnswerViewModelFactory
import com.example.masterapp.data.roomDatabase.QuestionnaireReminderViewModel
import com.example.masterapp.data.roomDatabase.QuestionnaireReminderViewModelFactory
import com.example.masterapp.data.roomDatabase.QuestionnaireRepository
import com.example.masterapp.presentation.BaseApplication
import com.example.masterapp.presentation.screen.PrivacyPolicyScreen
import com.example.masterapp.presentation.screen.SharedViewModel
import com.example.masterapp.presentation.screen.about.AboutScreen
import com.example.masterapp.presentation.screen.answerassessment.AnswerAssessmentPage
import com.example.masterapp.presentation.screen.assessments.AssessmentViewModel
import com.example.masterapp.presentation.screen.assessments.AssessmentViewModelFactory
import com.example.masterapp.presentation.screen.assessments.AssessmentPage
import com.example.masterapp.presentation.screen.answerassessment.AnswerAssessmentViewModel
import com.example.masterapp.presentation.screen.answerassessment.AnswerAssessmentViewModelFactory
import com.example.masterapp.presentation.screen.home.HomeScreen
import com.example.masterapp.presentation.screen.login.LoginScreen
import com.example.masterapp.presentation.screen.login.LoginViewModel
import com.example.masterapp.presentation.screen.login.LoginViewModelFactory
import com.example.masterapp.presentation.screen.profile.ProfileScreen
import com.example.masterapp.presentation.screen.register.RegisterScreen
import com.example.masterapp.presentation.screen.register.RegisterViewModel
import com.example.masterapp.presentation.screen.register.RegisterViewModelFactory
import com.example.masterapp.presentation.screen.results.ResultsScreen
import com.example.masterapp.presentation.screen.results.ResultsViewModel
import com.example.masterapp.presentation.screen.results.ResultsViewModelFactory
import com.example.masterapp.presentation.screen.settings.SettingsScreen
import com.example.masterapp.presentation.screen.settings.SettingsScreenViewModel
import com.example.masterapp.presentation.screen.settings.SettingsScreenViewModelFactory
import com.example.masterapp.presentation.screen.setup.SetupScreen
import com.example.masterapp.presentation.screen.setup.SetupScreenViewModel
import com.example.masterapp.presentation.screen.setup.SetupScreenViewModelFactory
import kotlinx.coroutines.launch

/**
 * Provides the navigation in the app.
 */
@Composable
fun AppNavigator(
    navController: NavHostController,
    healthConnectManager: HealthConnectManager,
    scaffoldState: ScaffoldState,
    authManager: AuthManager,
    notificationSettingsManager: NotificationSettingsManager,
) {
    Log.i("AppNavigator", "AppNavigator")
    Log.i("HealthConnectManagerPackage", healthConnectManager.context.packageManager.toString())
    val scope = rememberCoroutineScope()
    val sharedViewModel: SharedViewModel = viewModel()
    // Get the ApolloClient instance from the BaseApplication
    val app = LocalContext.current.applicationContext as BaseApplication
    val apolloClient = app.apolloClient
    val myHealthConnectManager = app.healthConnectManager
    val answerViewModelFactory = AnswerViewModelFactory(app.answerDatabase.answerDao)
    val answerViewModel: AnswerViewModel = viewModel(factory = answerViewModelFactory)
    val dao = app.answerDatabase.questionnaireReminderDao/* initialize or retrieve your DAO here */
    val repository = QuestionnaireRepository(dao)
    val viewModelFactory = QuestionnaireReminderViewModelFactory(repository)
    val questionnaireReminderViewModel: QuestionnaireReminderViewModel = viewModel(factory = viewModelFactory)

    // Initialize the ViewModelFactory for login and register
    val loginViewModelFactory = LoginViewModelFactory(apolloClient)
    val assessmentViewModelFactory = AssessmentViewModelFactory(apolloClient, healthConnectManager, repository)
    val registerViewModelFactory = RegisterViewModelFactory(apolloClient)
    // Create the LoginViewModel using the factory
    val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)
    val registerViewModel: RegisterViewModel = viewModel(factory = registerViewModelFactory)

    // Track the login state
    val isLoggedIn = authManager.isSignedIn()
    val preferencesHelper = app.preferencesHelper
    val hasCompletedSetup = preferencesHelper.isSetupComplete()

    val startDestination = when {
        !isLoggedIn -> Screen.LoginScreen.route
        isLoggedIn && !hasCompletedSetup -> Screen.SetupScreen.route
        else -> Screen.HomeScreen.route  // or whichever is the primary screen post-login
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.LoginScreen.route) {
            CrossfadeTransition(currentDestination = it) {
                LoginScreen(viewModel = loginViewModel,
                    onLoginSuccess = {
                        isLoggedIn
                        navigateToWelcomeScreen(navController)
                    }, onRegister = {
                        navigateToRegisterScreen(navController)
                    },
                    onAboutUs = {
                        navController.navigate(Screen.AboutScreen.route)
                    })
            }
        }

        composable(Screen.RegisterScreen.route) {
            CrossfadeTransition(currentDestination = it) {
                RegisterScreen(viewModel = registerViewModel, onLoginSuccess = {
                    isLoggedIn
                    navigateToWelcomeScreen(navController)
                }, onRegister = {
                    navigateToLoginScreen(navController)
                })
            }
        }

        composable(Screen.SetupScreen.route) {
            val viewModel: SetupScreenViewModel = viewModel(
                factory = SetupScreenViewModelFactory(
                    healthConnectManager = healthConnectManager,
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    notificationSettingsManager = notificationSettingsManager,
                )
            )

            val onPermissionsResult = { viewModel.initialLoad() }
            val permissionsLauncher =
                rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                    Log.i("AppNavigator", "Permissions result: $it")
                    onPermissionsResult()
                }

            CrossfadeTransition(currentDestination = it) {
                SetupScreen(
                    uiState = viewModel.uiState,
                    onPermissionsLaunch = { values ->
                        Log.d("WelcomeScreen", "Requesting permissions: $values")  // <-- Add this log
                        permissionsLauncher.launch(values)
                    },
                    viewModel = viewModel,
                    navController = navController,
                    notificationSettingsManager = notificationSettingsManager,
                )
                
            }
        }

        composable(Screen.HomeScreen.route) {
            CrossfadeTransition(currentDestination = it) {
                HomeScreen(
                    navController = navController,
                )
            }
        }

        composable(
            route = Screen.AssessmentScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "myapp://assessments"
                }
            )) {
            val assessmentViewModel: AssessmentViewModel = viewModel(factory = assessmentViewModelFactory)

            val onPermissionsResult = { assessmentViewModel.initialLoad() }
            val permissionsLauncher =
                rememberLauncherForActivityResult(assessmentViewModel.permissionsLauncher) {
                    Log.i("AppNavigator", "Permissions result: $it")
                    onPermissionsResult()
                }

            CrossfadeTransition(currentDestination = it) {
                AssessmentPage(
                    viewModel = assessmentViewModel,
                    navController = navController,
                    sharedViewModel= sharedViewModel,
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    healthConnectManager = healthConnectManager,)
                
            }
        }

        composable(Screen.ProfileScreen.route) {
            CrossfadeTransition(currentDestination = it) {
                ProfileScreen(
                    sharedViewModel = sharedViewModel,
                )
            }
        }
        // Add the navigation action for AnswerAssessmentScreen
        composable(Screen.AnswerAssessmentScreen.route) {
            // Create the ViewModel for AnswerAssessmentScreen
            val answerAssessmentViewModel: AnswerAssessmentViewModel = viewModel(
                factory = AnswerAssessmentViewModelFactory(
                    healthConnectManager = healthConnectManager,
                    answerViewModel = answerViewModel,
                    sharedViewModel = sharedViewModel,
                    questionnaireReminderViewModel = questionnaireReminderViewModel,
                    apolloClient = apolloClient
            ))

            // Pass the viewModel to the AssessmentPage composable
            CrossfadeTransition(currentDestination = it) {
                AnswerAssessmentPage(
                    viewModel = answerAssessmentViewModel,
                    sharedViewModel = sharedViewModel,
                    onGoToResults = {
                        navController.navigate(Screen.ResultsScreen.route)
                    },
                    onGoToAssessments = {
                        navController.navigate(Screen.AssessmentScreen.route)
                    }
                )
            }
        }

        composable(Screen.ResultsScreen.route) {
            val viewModel: ResultsViewModel = viewModel(
                factory = ResultsViewModelFactory(
                    answerViewModel = answerViewModel,
                    sharedViewModel = sharedViewModel,
                    apolloClient = apolloClient
                )
            )
            CrossfadeTransition(currentDestination = it) {
                ResultsScreen(
                    viewModel = viewModel,
                )
            }
        }

        composable(Screen.AboutScreen.route) {
            CrossfadeTransition(currentDestination = it) {
                AboutScreen()
            }
        }

        composable(Screen.SettingsScreen.route) {
            val viewModel: SettingsScreenViewModel = viewModel(
                factory = SettingsScreenViewModelFactory(
                    healthConnectManager = healthConnectManager
                )
            )

            val onPermissionsResult = { viewModel.refreshUI()}

            val permissionsLauncher =
                rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                    Log.i("AppNavigator", "Permissions result: $it")
                    onPermissionsResult()

                }

            CrossfadeTransition(currentDestination = it) {
                SettingsScreen(
                    healthConnectManager = healthConnectManager,
                    revokeAllPermissions = {
                        scope.launch {
                            healthConnectManager.revokeAllPermissions()
                        }
                    },
                    onPermissionsLaunch = { values ->
                        Log.d("WelcomeScreen", "Requesting permissions: $values")  // <-- Add this log
                        permissionsLauncher.launch(values)
                    },
                    viewModel = viewModel,
                    navController = navController,
                )
            }
        }

        composable(
            route = Screen.PrivacyPolicyScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    action = "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE"
                }
            )
        ) {
            CrossfadeTransition(currentDestination = it) {
                PrivacyPolicyScreen()
            }
        }
    }
}

private fun navigateToWelcomeScreen(navController: NavHostController) {
    navController.navigate(Screen.HomeScreen.route) {
        popUpTo(Screen.LoginScreen.route) {
            inclusive = true
        }

        launchSingleTop = true
    }
}

private suspend fun permissionGranted(healthConnectManager: HealthConnectManager): Boolean {
    return healthConnectManager.hasAllPermissions(healthConnectManager.permissions)
}

private fun navigateToRegisterScreen(navController: NavHostController) {
    navController.navigate(Screen.RegisterScreen.route)
}

private fun navigateToLoginScreen(navController: NavHostController) {
    navController.navigate(Screen.LoginScreen.route)
}

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun CrossfadeTransition(
    currentDestination: NavBackStackEntry,
    content: @Composable () -> Unit
) {
    Crossfade(targetState = currentDestination, label = "") {
Log.i("CrossfadeTransition", "CrossfadeTransition")
        Log.i("CrossfadeTransition", "currentDestination: $currentDestination")
        content()
    }
}