package com.example.masterapp.presentation.screen.assessments

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterapp.data.Assessment
import com.example.masterapp.type.QuestionnaireTypeEnum

@Composable
fun AssessmentCard(
    assessment: Assessment,
    onClick: (() -> Unit)? = null
) {
    val cardColor = getColorForType(assessment.assessmentType)
    Log.i("AssessmentCard", "Card color: $cardColor")
    Card(
        modifier = Modifier
            .padding(top= 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .fillMaxWidth(), // Make the card use the whole width of the screen
        elevation = 8.dp, // Increase the elevation for a shiny appearance
        shape = RoundedCornerShape(16.dp),
        backgroundColor = cardColor
    ) {
        Box {
            // Icon positioned at the top-right corner
            Icon(
                when (assessment.assessmentType) {
                    QuestionnaireTypeEnum.anxiety -> Icons.Default.Mood
                    QuestionnaireTypeEnum.depression -> Icons.Default.Accessibility
                    QuestionnaireTypeEnum.physical_health -> Icons.Default.FitnessCenter
                    QuestionnaireTypeEnum.general_health -> Icons.Default.WbSunny
                    else -> {
                        Icons.Default.Mood  }
                },
                contentDescription = "Assessment Type",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd) // Align the icon to the top-right corner
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display assessment title
                Text(
                    text = assessment.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center // Center the text horizontally
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Display assessment type in a user-friendly format
                Text(
                    text = "Type: ${getFormattedType(assessment.assessmentType)}",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center // Center the text horizontally
                )

                // Display assessment frequency
                Text(
                    text = "Recurring: ${assessment.frequency}",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center // Center the text horizontally
                )
            }
        }
    }
}





private fun getFormattedType(assessmentType: QuestionnaireTypeEnum?): String {
    return when (assessmentType) {
        QuestionnaireTypeEnum.anxiety -> "Anxiety"
        QuestionnaireTypeEnum.depression -> "Depression"
        QuestionnaireTypeEnum.physical_health -> "Physical health"
        QuestionnaireTypeEnum.general_health -> "General health"
        else -> "Unknown Type" // Add a default value for unrecognized types, if needed.
    }
}

private fun getColorForType(assessmentType: QuestionnaireTypeEnum?): Color {
    return when (assessmentType) {
        QuestionnaireTypeEnum.anxiety -> Color(0xFF86C6E5) // Blue with alpha FF (full opacity)
        QuestionnaireTypeEnum.depression -> Color(0xFF8BC34A) // Green with alpha FF (full opacity)
        QuestionnaireTypeEnum.physical_health -> Color(0xFFE1BEE7) // Lavender with alpha FF (full opacity)
        QuestionnaireTypeEnum.general_health -> Color(0xFFE57373) // Red with alpha FF (full opacity)
        else -> Color.Gray // Default color for unrecognized types
    }
}




