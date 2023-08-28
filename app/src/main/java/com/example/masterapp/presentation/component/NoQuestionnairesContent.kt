package com.example.masterapp.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.masterapp.R

@Composable
fun NoQuestionnairesContent(
    type: String // "Available" or "Completed"
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground_dup),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (type == "available") {
                Text(
                    text = "We´re so sorry, there´s currently no questionnaires available at the moment.",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "We will send you a notification when our therapists add one! In the meantime keep looking after yourself and take a look at your results in the result section",
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "No questionnaires completed at the moment.",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Go to available questionnaires and answer them to get started!",
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center
                )
            }


        }
    }
}
