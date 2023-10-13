package com.example.masterapp.presentation.screen.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterapp.R
import com.example.masterapp.presentation.screen.SharedViewModel

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
            .padding(top = 50.dp, end = 16.dp, bottom = 100.dp, start = 16.dp)
            .background(Color.White),
        shape = RoundedCornerShape(50.dp),
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.primary),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title "Your Account"
            Text(
                text = "My Account",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
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
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Full Name with label and box
            Text(
                text = "Name",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                )
            OutlinedTextField(
                value = userProfile?.fullName ?: "No Name",
                onValueChange = {},
                readOnly = true,
                textStyle = TextStyle(fontSize = 24.sp, color = MaterialTheme.colors.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(Color.White)
                    .border(
                        width = 3.dp,
                        color = Color.White,
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Email Address with label and box
            Text(
                text = "Email",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            OutlinedTextField(
                value = userProfile?.email ?: "No Email",
                onValueChange = {},
                readOnly = true,
                textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colors.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(Color.White)
                    .border(
                        width = 3.dp,
                        color = Color.White,
                    )
            )
        }
    }
}



