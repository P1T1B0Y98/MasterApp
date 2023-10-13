package com.example.masterapp.presentation.screen.assessments

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.masterapp.R
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
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .fillMaxWidth().height(125.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(30.dp),
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                val truncatedTitle = truncateText(assessment.title, 20)
                Text(
                    text = truncatedTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Left,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Type: ${getFormattedType(assessment.assessmentType)}",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "00 / 0${assessment.assessmentSchema?.size}",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    color = Color.White
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.online_poll_survey_icon),
                contentDescription = "Questionnaire",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .size(100.dp)// Align the icon to the bottom-right corner
            )

        }
    }
}

private fun truncateText(text: String, maxLength: Int): String {
    return if (text.length > maxLength) {
        text.substring(0, maxLength - 3) + "..."
    } else {
        text
    }
}

private fun getFormattedType(assessmentType: QuestionnaireTypeEnum?): String {
    return when (assessmentType) {
        QuestionnaireTypeEnum.anxiety -> "Anxiety"
        QuestionnaireTypeEnum.depression -> "Depression"
        QuestionnaireTypeEnum.physical_health -> "Physical health"
        QuestionnaireTypeEnum.general_health -> "General health"
        else -> "Unknown Type" 
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




