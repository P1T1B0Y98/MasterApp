package com.example.masterapp.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DailyTipCard(tip: String) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colors.primary, MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Tip of the Day",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primaryVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tip,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
        }
    }
}
