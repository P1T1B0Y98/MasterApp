package com.example.masterapp.presentation.screen.answerassessment

import com.example.masterapp.data.Assessment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterapp.R

@Composable
fun AnswerAssessmentInformation(
    assessment: Assessment,
    viewModel: AnswerAssessmentViewModel,
    onGoBack: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = 50.dp,
                            bottomEnd = 50.dp
                        )
                    )
                    .background(MaterialTheme.colors.primary),
            ) {
            IconButton(
                onClick = {
                    // Navigate back to the questionnaires screen
                    onGoBack()
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground_dup),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

            // Display questionnaire title and number of questions
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 300.dp, start = 15.dp)
            ) {
                Text(
                    text = assessment.title,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,

                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Description:",
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = assessment.description,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Type:",
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = assessment.assessmentType.toString(),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                    fontSize = 15.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Questions:",
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = assessment.assessmentSchema?.size.toString(),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                    fontSize = 15.sp,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = { viewModel.answerAssessment(assessment) },
                        modifier = Modifier
                            .width(100.dp)
                            .height(75.dp),
                        colors = MaterialTheme.colors.primary.let {
                            ButtonDefaults.buttonColors(
                                backgroundColor = it,
                                contentColor = Color.White
                            )
                        }
                    ) {
                        Text(text = "Start")
                    }
                }
            }

    }
}



