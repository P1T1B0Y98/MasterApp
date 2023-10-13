package com.example.masterapp.presentation.navigation
import com.example.masterapp.R

/**
 * Represent all Screens in the app.
 *
 * @param route The route string used for Compose navigation
 * @param titleId The ID of the string resource to display as a title
 * @param hasMenuItem Whether this Screen should be shown as a menu item in the left-hand menu (not
 *     all screens in the navigation graph are intended to be directly reached from the menu).
 */
enum class Screen(val route: String, val titleId: Int, val hasMenuItem: Boolean = true) {
    HomeScreen("home", R.string.home_screen, hasMenuItem = true),
    LoginScreen("login", R.string.login_screen, hasMenuItem = false),
    AssessmentScreen("assessment", R.string.assessment_screen, hasMenuItem = true),
    RegisterScreen("register", R.string.register_screen, hasMenuItem = false),
    AnswerAssessmentScreen("answer_assessment", R.string.answer_assessment_screen, hasMenuItem = false),
    ResultsScreen("results", R.string.results_screen, hasMenuItem = true),
    SetupScreen("setup", R.string.setup_screen, hasMenuItem = false),
    PrivacyPolicyScreen("privacy", R.string.privacy_policy_screen),
    ProfileScreen("user_profile", R.string.user_profile_screen, hasMenuItem = true),
    SettingsScreen("settings", R.string.settings_screen, hasMenuItem = true),
    AboutScreen("about", R.string.about_screen, hasMenuItem = true),
}
