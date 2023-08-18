package com.example.masterapp.presentation.screen.register


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterapp.R
import com.example.masterapp.presentation.screen.register.RegisterViewModel
import com.example.masterapp.presentation.theme.Lavender
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(viewModel: RegisterViewModel, onLoginSuccess: () -> Unit, onRegister: () -> Unit) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 64.dp), // Ad
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = {
                val email = emailState.value
                val password = passwordState.value
                coroutineScope.launch {
                    // Call the suspend function in the ViewModel using coroutines
                    viewModel.register(email, password) { success ->
                        if (success) {
                            Log.i("RegisterScreen", "Registered yourself success")
                            onRegister()
                        }
                        else {
                            print("Login failed")
                        }
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Register")
        }
  /*      Image(
            painter = painterResource(id = R.drawable.stress_management),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        // Add the App Name and Slogan
        Text(
            text = "HealthPulse",
            style = MaterialTheme.typography.h4,
            color = Color(0xFF86C6E5),
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Text(
            text = "Empowering Minds, Healing Hearts",
            style = MaterialTheme.typography.subtitle1,
            color = Color(0xFF86C6E5),
            modifier = Modifier.padding(bottom = 32.dp)
        )*/

        TextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ClickableText(
            text = AnnotatedString("Already have an account? Login here"),
            style = TextStyle(
                color = Lavender, // Customize the link text color here
                textDecoration = TextDecoration.Underline,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold // Optional, you can adjust the font weight
            ),
            onClick = {
                // Navigate to the registration screen
                onRegister()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
        )
    }
}
