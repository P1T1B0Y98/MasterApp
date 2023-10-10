package com.example.masterapp.presentation.component

import com.example.masterapp.presentation.screen.results.AssessmentResponses
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GeneralInformationCard(
    answer: AssessmentResponses,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "General Information",
                    style = MaterialTheme.typography.h6,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer (modifier = Modifier.height(8.dp))
                LabeledValue("Questionnaire: ", answer.questionnaire.title)
                LabeledValue("Completed: ", answer.completed)
                LabeledValue("Type: ", answer.questionnaire.questionnaireType.toString())
            }

            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = "Information",
                tint = Color.White,
                modifier = Modifier.padding(start = 40.dp).size(40.dp)

            )
        }
    }
}



@Composable
fun LabeledValue(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body2,
            fontSize = 16.sp,
        )
    }
}


