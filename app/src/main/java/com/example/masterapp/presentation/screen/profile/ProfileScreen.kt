package com.example.masterapp.presentation.screen.profile

import UserProfile
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterapp.R
import com.example.masterapp.presentation.screen.SharedViewModel
import com.example.masterapp.presentation.theme.MasterAppTheme

@Composable
fun ProfileScreen(
    sharedViewModel: SharedViewModel,
) {
    // Get the user profile
    val userProfile = sharedViewModel.getUserProfile()
    Log.i("ProfileScreen", "userProfile: $userProfile")

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title "Your Account"
            Text(
                text = "Your Account",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Profile Picture or Person Logo inside a Card for frame effect
            Card(
                modifier = Modifier.size(120.dp),
                elevation = 4.dp,
                shape = CircleShape // Optional, for rounded frame
            ) {
                val image = painterResource(id = R.drawable.physical_therapy) // Replace with your person logo drawable ID
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Full Name with label and box
            Text(text = "Name", style = MaterialTheme.typography.caption)
            OutlinedTextField(
                value = userProfile?.fullName ?: "No Name",
                onValueChange = {},
                readOnly = true,
                textStyle = TextStyle(fontSize = 24.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            // Email Address with label and box
            Text(text = "Email", style = MaterialTheme.typography.caption)
            OutlinedTextField(
                value = userProfile?.email ?: "No Email",
                onValueChange = {},
                readOnly = true,
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}



