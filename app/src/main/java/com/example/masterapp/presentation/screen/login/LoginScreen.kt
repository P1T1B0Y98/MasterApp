package com.example.masterapp.presentation.screen.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterapp.R
import com.example.masterapp.presentation.theme.Lavender
import kotlinx.coroutines.launch
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel,
                onLoginSuccess: () -> Unit,
                onRegister: () -> Unit,
                onAboutUs: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val emailState = remember { mutableStateOf("petter@petter.no") }
    val passwordState = remember { mutableStateOf("wilshere") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    keyboardController?.hide()
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 64.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground_dup),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )

            Text(
                text = "MindSync",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF86C6E5),
                modifier = Modifier.padding(bottom = 5.dp)
            )

            Text(
                text = "Syncing Mind & Metrics",
                style = MaterialTheme.typography.subtitle1,
                color = Color(0xFF86C6E5),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            TextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val email = emailState.value
                    val password = passwordState.value
                    Log.i("LoginScreen", "email: $email, password: $password")
                    coroutineScope.launch {
                        viewModel.login(email, password) { success ->
                            if (success) {
                                onLoginSuccess()
                            } else {
                                print("Login failed")
                            }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .size(width = 200.dp, height = 50.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_login_24),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Login")
            }

            ClickableText(
                text = AnnotatedString("First time? Signup here"),
                style = TextStyle(
                    color = Lavender,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                onClick = {
                    onRegister()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )

            ClickableText(
                text = AnnotatedString("About us"),
                style = TextStyle(
                    color = Lavender,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                onClick = {
                    onAboutUs()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
        }
    }
}
