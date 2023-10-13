package com.example.masterapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.masterapp.R

/**
 * Shows the privacy policy.
 */
@Composable
fun PrivacyPolicyScreen() {
    val sections = listOf(
        createPrivacyPolicySection(
            title = "Introduction",
            content = "Privacy policy for the MindSync application..."
        ),
        createPrivacyPolicySection(
            title = "Information Collected",
            content = "Name, email, wearable data..."
        ),
        createPrivacyPolicySection(
            title = "Data Collection",
            content = "We collect user data for the purpose of improving our services..."
        ),
        createPrivacyPolicySection(
            title = "Data Sharing",
            content = "We may share user data with trusted third-party partners for analytics..."
        ),
        createPrivacyPolicySection(
            title = "Data Usage",
            content = "To be decided..."
        ),
        createPrivacyPolicySection(
            title = "User Control",
            content = "To be decided..."
        ),
        // Add more PrivacyPolicySection for other sections of your privacy policy
    )
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
                .height(250.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 50.dp, // Adjust this value for the desired corner radius
                        bottomEnd = 50.dp // Adjust this value for the desired corner radius
                    )
                )
                .background(MaterialTheme.colors.primary),
        ) {
            Spacer(modifier = Modifier.height(30.dp))

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
                    modifier = Modifier.size(125.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MindSync",
                style = MaterialTheme.typography.h4,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth().padding(top = 250.dp)
                .padding(16.dp)
        ) {

            items(sections) { section ->
                PrivacyPolicySectionCard(section = section)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun PrivacyPolicySectionCard(section: PrivacyPolicySection) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = MaterialTheme.colors.primary,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.h6,
                    color = Color.White,
                )

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color.White,
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = section.content,
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                )
            }
        }
    }
}

data class PrivacyPolicySection(
    val title: String,
    val content: String
)

fun createPrivacyPolicySection(title: String, content: String): PrivacyPolicySection {
    return PrivacyPolicySection(title, content)
}
