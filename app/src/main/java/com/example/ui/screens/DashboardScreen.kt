package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AssignmentPriority
import com.example.ui.navigation.Screen
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun DashboardScreen(
    viewModel: CampusViewModel,
    onNavigate: (Screen) -> Unit
) {
    val timetableClasses by viewModel.timetableClasses.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val assignments by viewModel.assignments.collectAsState()
    val exams by viewModel.exams.collectAsState()
    val today = viewModel.currentDayOfWeek

    // Filter today's classes
    val todaysClasses = timetableClasses.filter { it.dayOfWeek.equals(today, ignoreCase = true) }

    // Attendance warnings
    val warningRecords = attendanceRecords.filter { it.isBelowTarget }
    val overallAttended = attendanceRecords.sumOf { it.attendedClasses }
    val overallTotal = attendanceRecords.sumOf { it.totalClasses }
    val overallPercentage = if (overallTotal > 0) (overallAttended.toFloat() / overallTotal) * 100f else 0f

    // Upcoming pending assignment
    val now = System.currentTimeMillis()
    val upcomingAssignment = assignments
        .filter { !it.isCompleted && it.deadlineTimestamp >= (now - 3600000) }
        .minByOrNull { it.deadlineTimestamp }
        ?: assignments.firstOrNull { !it.isCompleted }

    // Next exam
    val upcomingExam = exams
        .filter { it.examTimestamp >= now }
        .minByOrNull { it.examTimestamp }
        ?: exams.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Hero Banner Card
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.campus_hero_banner),
                        contentDescription = "University Campus",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.2f),
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Text(
                                text = "Today is $today",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Welcome to CampusMate",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Stay organized, boost attendance & ace your semester.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Attendance Alert or Summary Banner
        item {
            if (warningRecords.isNotEmpty()) {
                val firstWarn = warningRecords.first()
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.ATTENDANCE) }
                        .testTag("attendance_warning_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Attendance Alert: ${warningRecords.size} Course(s) Below Target",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${firstWarn.subject} is at ${"%.1f".format(firstWarn.percentage)}% (Target: ${firstWarn.targetPercentage}%). Need to attend ${firstWarn.classesNeededToReachTarget} more classes.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Attendance"
                        )
                    }
                }
            } else {
                OutlinedCard(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.ATTENDANCE) }
                        .testTag("attendance_good_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Overall Attendance: ${"%.1f".format(overallPercentage)}%",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "All courses are on track with target requirements.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Attendance",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Access Grid
        item {
            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickButton(
                        title = "Timetable",
                        subtitle = "Classes & rooms",
                        icon = Icons.Default.CalendarMonth,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.TIMETABLE) }
                    )
                    QuickButton(
                        title = "Attendance",
                        subtitle = "Track & target %",
                        icon = Icons.Default.BarChart,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.ATTENDANCE) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickButton(
                        title = "Assignments",
                        subtitle = "Tasks & deadlines",
                        icon = Icons.Default.Assignment,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.ASSIGNMENTS) }
                    )
                    QuickButton(
                        title = "Exams",
                        subtitle = "Countdown timer",
                        icon = Icons.Default.HourglassTop,
                        color = Color(0xFFE11D48),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.EXAMS) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickButton(
                        title = "Study Timer",
                        subtitle = "Pomodoro focus",
                        icon = Icons.Default.Timer,
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.STUDY_TIMER) }
                    )
                    QuickButton(
                        title = "GPA Calc",
                        subtitle = "Semester & CGPA",
                        icon = Icons.Default.Calculate,
                        color = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.GPA_CALCULATOR) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickButton(
                        title = "Contacts",
                        subtitle = "Emergency & office",
                        icon = Icons.Default.Emergency,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.CONTACTS) }
                    )
                    QuickButton(
                        title = "How to Use",
                        subtitle = "Step guide",
                        icon = Icons.Default.MenuBook,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.USER_GUIDE) }
                    )
                }
            }
        }

        // Today's Classes Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Schedule ($today)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .clickable { onNavigate(Screen.TIMETABLE) }
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (todaysClasses.isEmpty()) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "No classes scheduled for today. Great day to study!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    todaysClasses.forEach { cls ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(Screen.TIMETABLE) },
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp, 44.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(android.graphics.Color.parseColor(cls.colorHex)))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cls.subject,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${cls.startTime} - ${cls.endTime} • ${cls.room}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Teacher: ${cls.teacher}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Text(
                                        text = cls.classType,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Upcoming Assignment & Exam in 2 cards
        item {
            Text(
                text = "Upcoming Deadlines & Tests",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Assignment Card
                if (upcomingAssignment != null) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.ASSIGNMENTS) },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Assignment,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Assignment Due",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                val priorityColor = when (upcomingAssignment.priority) {
                                    AssignmentPriority.HIGH -> MaterialTheme.colorScheme.error
                                    AssignmentPriority.MEDIUM -> MaterialTheme.colorScheme.secondary
                                    AssignmentPriority.LOW -> MaterialTheme.colorScheme.tertiary
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = priorityColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${upcomingAssignment.priority.name} Priority",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = priorityColor,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = upcomingAssignment.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "${upcomingAssignment.subject} • Due ${CampusViewModel.formatTimestamp(upcomingAssignment.deadlineTimestamp)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Exam Card
                if (upcomingExam != null) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.EXAMS) },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE11D48).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = Color(0xFFE11D48)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = upcomingExam.subject,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${upcomingExam.room} • ${CampusViewModel.formatDateOnly(upcomingExam.examTimestamp)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFE11D48),
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = upcomingExam.remainingText(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Focus / Pomodoro CTA
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Screen.STUDY_TIMER) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ready to Focus?",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Start a 25-minute Pomodoro study block to maximize productivity.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    FilledTonalButton(
                        onClick = { onNavigate(Screen.STUDY_TIMER) }
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Study")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("quick_${title.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    fontSize = 14.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}
