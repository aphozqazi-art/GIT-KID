package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.Screen

data class GuideStep(
    val stepNumber: Int,
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val summary: String,
    val instructions: List<String>,
    val targetScreen: Screen
)

@Composable
fun UserGuideScreen(
    onNavigate: (Screen) -> Unit
) {
    val steps = listOf(
        GuideStep(
            stepNumber = 1,
            title = "Set Up Your Weekly Timetable",
            icon = Icons.Default.CalendarMonth,
            color = Color(0xFF2563EB),
            summary = "Keep your Monday-Friday class schedule, lecture halls, and professor names accessible in seconds.",
            instructions = listOf(
                "Tap on the 'Timetable' tab from the bottom bar or dashboard.",
                "Switch between Monday through Saturday tabs to see each day's agenda.",
                "Tap the '+' FAB button to add class timing, teacher name, room, and lecture type.",
                "The dashboard automatically displays today's schedule based on current day."
            ),
            targetScreen = Screen.TIMETABLE
        ),
        GuideStep(
            stepNumber = 2,
            title = "Track Attendance & Avoid Shortage",
            icon = Icons.Default.BarChart,
            color = Color(0xFF059669),
            summary = "Automatic attendance % calculations and smart alerts telling you exactly how many classes you must attend to meet your 75% or 80% criteria.",
            instructions = listOf(
                "Open 'Attendance' to view subject-wise and overall university attendance.",
                "Use the quick '+ Attended' or '+ Missed' buttons after each lecture.",
                "If your attendance drops below target, CampusMate calculates consecutive classes needed.",
                "If you are safe, CampusMate lets you know how many classes you can safely miss."
            ),
            targetScreen = Screen.ATTENDANCE
        ),
        GuideStep(
            stepNumber = 3,
            title = "Stay on Top of Assignments",
            icon = Icons.Default.Assignment,
            color = Color(0xFFD97706),
            summary = "Categorize homework and term projects by priority (High/Medium/Low) and filter by Pending/Completed.",
            instructions = listOf(
                "Tap the 'Assignments' tab to view deadlines.",
                "Check off assignments as you submit them with the checkbox.",
                "Filter between All, Pending, and Completed assignments to prioritize work.",
                "High priority tasks display in bold red alerts on your dashboard."
            ),
            targetScreen = Screen.ASSIGNMENTS
        ),
        GuideStep(
            stepNumber = 4,
            title = "Countdown to Exams & Tests",
            icon = Icons.Default.HourglassTop,
            color = Color(0xFFE11D48),
            summary = "Live day-by-day countdowns for Midterms and Finals with hall venues and topic checklists.",
            instructions = listOf(
                "Access 'Exams' to view countdown timer badges (e.g. 'In 4 days', 'Tomorrow').",
                "Add exam details including room number, seat/desk number, and syllabus chapters.",
                "The dashboard warns you when an exam is starting within the week."
            ),
            targetScreen = Screen.EXAMS
        ),
        GuideStep(
            stepNumber = 5,
            title = "Focus with Pomodoro Study Timer",
            icon = Icons.Default.Timer,
            color = Color(0xFF7C3AED),
            summary = "Boost productivity and retention using 25-minute study intervals followed by 5-minute rejuvenating breaks.",
            instructions = listOf(
                "Open 'Study Timer' and choose the course you are studying for.",
                "Hit 'Start' to begin a 25-minute Pomodoro focus block.",
                "Every completed block increases your streak count and logs time to your study history.",
                "Every 4 sessions triggers a relaxing 15-minute long break."
            ),
            targetScreen = Screen.STUDY_TIMER
        ),
        GuideStep(
            stepNumber = 6,
            title = "Calculate Semester GPA & Cumulative CGPA",
            icon = Icons.Default.Calculate,
            color = Color(0xFF0284C7),
            summary = "Accurately compute 4.0 scale GPA based on credit hours and letter grades.",
            instructions = listOf(
                "Select 'GPA Calc' from Quick Access or Campus Tools.",
                "Enter course credit hours (e.g., 3.0 or 4.0) and letter grade (A+, A, B, etc.).",
                "Instant live GPA output with academic standing (Dean's List / Honors).",
                "Switch to Cumulative CGPA tab to calculate overall degree standing."
            ),
            targetScreen = Screen.GPA_CALCULATOR
        ),
        GuideStep(
            stepNumber = 7,
            title = "One-Tap Campus & Emergency Contacts",
            icon = Icons.Default.Emergency,
            color = Color(0xFFDC2626),
            summary = "Direct one-tap phone dialer and emails for Campus Security, Health Clinic, Registrar, and Dean.",
            instructions = listOf(
                "Tap 'Contacts' to access pre-configured campus safety and administrative offices.",
                "Tap 'Call' to launch your phone dialer instantly without searching directory papers.",
                "Add your personal academic advisors and department offices anytime."
            ),
            targetScreen = Screen.CONTACTS
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("user_guide_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "How to Use CampusMate",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Follow these 7 simple steps to master your university life and stay ahead of coursework.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        itemsIndexed(steps) { _, step ->
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = step.color,
                            contentColor = Color.White,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${step.stepNumber}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = step.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = step.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            step.instructions.forEach { instr ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(
                                        text = "• ",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = step.color
                                    )
                                    Text(
                                        text = instr,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onNavigate(step.targetScreen) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Open ${step.targetScreen.title}")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
