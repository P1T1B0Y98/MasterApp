package com.example.masterapp.presentation.navigation

import AuthManager
import android.util.Log
import android.widget.Space
import com.example.masterapp.presentation.theme.MasterAppTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.masterapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController,
    authManager: AuthManager
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if the user is logged in
    val isLoggedIn = authManager.isSignedIn()
    Log.i("Drawer", "isLoggedIn: $isLoggedIn")
    // Render the Drawer and the button's Image if the user is logged in
    if (isLoggedIn) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.secondary)
                    .padding(8.dp) // You can adjust the padding as needed
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center

                ) {
                    Image(
                        modifier = Modifier
                            .width(96.dp)
                            .clickable {
                                navController.navigate(Screen.HomeScreen.route) {
                                    navController.graph.startDestinationRoute?.let { route ->
                                        popUpTo(route) {
                                            saveState = true
                                        }
                                    }
                                    launchSingleTop = false
                                    restoreState = false
                                }
                                scope.launch {
                                    scaffoldState.drawerState.close()
                                }
                            },
                        painter = painterResource(id = R.drawable.stress_management),
                        contentDescription = stringResource(id = R.string.app_name)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                    ),
                    text = stringResource(id = R.string.app_name)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Screen.values().filter { it.hasMenuItem }.forEach { item ->
                DrawerItem(
                    item = item,
                    selected = item.route == currentRoute,
                    onItemClick = {
                        Log.i("Drawer", "item.route: ${item.route}")
                        navController.navigate(item.route) {
                            // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = false
                            restoreState = false
                        }
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }

                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Regular Button for Logout
            Button(
                onClick = {
                    authManager.logout()
                    navController.navigate(Screen.LoginScreen.route) {
                        // Clear the back stack including the start destination
                        popUpTo(navController.graph.startDestinationRoute!!) {
                            inclusive = true
                            saveState = true
                        }
                    }
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_logout_24), // Assuming "ic_logout" is your logout icon resource
                        contentDescription = "Logout Icon",
                        modifier = Modifier.size(24.dp) // Adjust the icon size if necessary
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Some space between icon and text
                    Text("Logout")
                }
            }

            // Provide some space after the button
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

