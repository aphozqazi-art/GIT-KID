package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.CampusViewModel
import com.example.ui.viewmodel.PomodoroMode

@Composable
fun StudyTimerScreen(
    viewModel: CampusViewModel
) {
    val mode by viewModel.pomodoroMode.collectAsState()
    val secondsRemaining by viewModel.timerSecondsRemaining.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val completedCount by viewModel.pomodoroCount.collectAsState()
    val currentSubject by viewModel.timerSubject.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val studyHistory by viewModel.studySessions.collectAsState()

    val totalSeconds = mode.defaultMinutes * 60
    val progress = (secondsRemaining.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "timerProgress")

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)

    val timerColor = when (mode) {
        PomodoroMode.STUDY -> MaterialTheme.colorScheme.primary
        PomodoroMode.SHORT_BREAK -> MaterialTheme.colorScheme.tertiary
        PomodoroMode.LONG_BREAK -> MaterialTheme.colorScheme.secondary
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("study_timer_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Mode Tabs (Study 25m, Short Break 5m, Long Break 15m)
            TabRow(
                selectedTabIndex = mode.ordinal,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                PomodoroMode.entries.forEach { m ->
                    Tab(
                        selected = mode == m,
                        onClick = { viewModel.switchPomodoroMode(m) },
                        text = {
                            Text(
                                text = "${m.title} (${m.defaultMinutes}m)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    )
                }
            }
        }

        // Circular Timer Visual
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .testTag("timer_circle"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 14.dp
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = timerColor,
                    strokeWidth = 14.dp
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = timerColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isRunning) "Focus in progress..." else "Paused",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = timerColor
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Controls (Play / Pause, Reset, Skip)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        if (isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .width(160.dp)
                        .testTag("toggle_timer_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = timerColor)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "PAUSE" else "START",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                IconButton(
                    onClick = { viewModel.skipTimer() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Skip Session",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Subject selector chips
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Studying for Subject:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                val subjects = if (attendanceRecords.isNotEmpty()) {
                    attendanceRecords.map { it.subject }
                } else {
                    listOf("Data Structures", "Algorithms", "Computer Networks", "Math", "General")
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects) { subj ->
                        FilterChip(
                            selected = currentSubject == subj,
                            onClick = { viewModel.setTimerSubject(subj) },
                            label = { Text(subj) }
                        )
                    }
                }
            }
        }

        // Pomodoro Streak Counter
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Today's Study Streak",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Every 4 sessions triggers a 15-min deep rest break.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(
                            text = "🍅 $completedCount Done",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Session History
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Recent Study Sessions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (studyHistory.isEmpty()) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Complete your first 25-minute Pomodoro session to log study time!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    studyHistory.take(5).forEach { session ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${session.subject} • ${session.durationMinutes} mins",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = CampusViewModel.formatTimestamp(session.timestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
