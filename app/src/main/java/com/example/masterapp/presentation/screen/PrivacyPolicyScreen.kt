package com.example.masterapp.presentation.screen

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterapp.R
import com.example.masterapp.presentation.theme.MasterAppTheme

/**
 * Shows the privacy policy.
 */
@Composable
fun PrivacyPolicyScreen() {
    val sections = listOf(
        createPrivacyPolicySection(
            title = "Data Collection",
            content = "We collect user data for the purpose of improving our services..."
        ),
        createPrivacyPolicySection(
            title = "Data Sharing",
            content = "We may share user data with trusted third-party partners for analytics..."
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
            title = "Data Collection",
            content = "We collect user data for the purpose of improving our services..."
        ),
        createPrivacyPolicySection(
            title = "Data Sharing",
            content = "We may share user data with trusted third-party partners for analytics..."
        ),
        createPrivacyPolicySection(
            title = "Data Collection",
            content = "We collect user data for the purpose of improving our services..."
        ),
        createPrivacyPolicySection(
            title = "Data Sharing",
            content = "We may share user data with trusted third-party partners for analytics..."
        ),
        // Add more PrivacyPolicySection for other sections of your privacy policy
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp)
                    .padding(bottom = 0.dp),
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.health_connect_logo)
            )

            Text (
                text = "Mind Sync - Privacy Policy",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(sections) { section ->
            PrivacyPolicySectionCard(section = section)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
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
