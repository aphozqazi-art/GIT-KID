package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.Screen

@Composable
fun CampusHubScreen(
    onNavigate: (Screen) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("campus_hub_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Campus Tools & Utilities",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Access all university study aids, calculators, and safety tools.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            HubItemCard(
                title = "Exam Countdown",
                description = "Track dates, examination venues, and live day-by-day countdowns for midterms and finals.",
                icon = Icons.Default.HourglassTop,
                badge = "Test Prep",
                iconColor = Color(0xFFE11D48),
                onClick = { onNavigate(Screen.EXAMS) }
            )
        }

        item {
            HubItemCard(
                title = "Study Timer (Pomodoro)",
                description = "25-minute focus intervals and 5-minute restorative breaks to maximize academic retention.",
                icon = Icons.Default.Timer,
                badge = "Productivity",
                iconColor = Color(0xFF7C3AED),
                onClick = { onNavigate(Screen.STUDY_TIMER) }
            )
        }

        item {
            HubItemCard(
                title = "GPA & CGPA Calculator",
                description = "Interactive 4.0 scale semester GPA calculator with cumulative degree CGPA projections.",
                icon = Icons.Default.Calculate,
                badge = "Academic",
                iconColor = Color(0xFF0284C7),
                onClick = { onNavigate(Screen.GPA_CALCULATOR) }
            )
        }

        item {
            HubItemCard(
                title = "Emergency & Campus Contacts",
                description = "24/7 Campus Security patrol, University Health & Ambulance, Registrar, and Dean helplines.",
                icon = Icons.Default.Emergency,
                badge = "Safety & Support",
                iconColor = MaterialTheme.colorScheme.error,
                onClick = { onNavigate(Screen.CONTACTS) }
            )
        }

        item {
            HubItemCard(
                title = "How-to-Use Step Guide",
                description = "Step-by-step tutorial explaining how to configure your timetable, track attendance, and use all features.",
                icon = Icons.Default.MenuBook,
                badge = "User Guide",
                iconColor = MaterialTheme.colorScheme.primary,
                onClick = { onNavigate(Screen.USER_GUIDE) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HubItemCard(
    title: String,
    description: String,
    icon: ImageVector,
    badge: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("hub_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = iconColor.copy(alpha = 0.12f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
