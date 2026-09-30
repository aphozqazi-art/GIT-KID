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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exam
import com.example.ui.components.EmptyStateView
import com.example.ui.viewmodel.CampusViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: CampusViewModel
) {
    val exams by viewModel.exams.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var examToEdit by remember { mutableStateOf<Exam?>(null) }
    var examToDelete by remember { mutableStateOf<Exam?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_exam_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Exam")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("exam_screen")
        ) {
            if (exams.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.HourglassTop,
                    title = "No Upcoming Exams Scheduled",
                    description = "Stay stress-free! Tap '+' to log your upcoming midterms, finals, or quizzes and see live day countdowns.",
                    actionText = "Schedule Exam",
                    onActionClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Exam Timetable & Live Countdowns",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Track remaining days, exam venues, and study syllabi.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(exams, key = { it.id }) { item ->
                        ExamCountdownCard(
                            exam = item,
                            onEdit = { examToEdit = item },
                            onDelete = { examToDelete = item }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Add or Edit Dialog
    if (showAddDialog || examToEdit != null) {
        val initial = examToEdit
        AddEditExamDialog(
            initial = initial,
            onDismiss = {
                showAddDialog = false
                examToEdit = null
            },
            onSave = { subject, timestamp, room, topics, seatNumber ->
                if (initial != null) {
                    viewModel.updateExam(
                        initial.copy(
                            subject = subject,
                            examTimestamp = timestamp,
                            room = room,
                            topics = topics,
                            seatNumber = seatNumber
                        )
                    )
                } else {
                    viewModel.addExam(subject, timestamp, room, topics, seatNumber)
                }
                showAddDialog = false
                examToEdit = null
            }
        )
    }

    // Confirm Delete Dialog
    if (examToDelete != null) {
        AlertDialog(
            onDismissRequest = { examToDelete = null },
            title = { Text("Delete Exam?") },
            text = { Text("Are you sure you want to remove '${examToDelete?.subject}' from your exam schedule?") },
            confirmButton = {
                Button(
                    onClick = {
                        examToDelete?.let { viewModel.deleteExam(it) }
                        examToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { examToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExamCountdownCard(
    exam: Exam,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val days = exam.daysRemaining()
    val badgeColor = when {
        days < 0 -> Color(0xFF6B7280) // Passed
        days <= 2 -> Color(0xFFDC2626) // Urgent (< 2 days)
        days <= 7 -> Color(0xFFD97706) // Coming up (< 7 days)
        else -> Color(0xFF059669)     // Safe (> 7 days)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_card_${exam.id}"),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Countdown Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeColor,
                    contentColor = Color.White
                ) {
                    Text(
                        text = exam.remainingText(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = exam.subject,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "📅 Date: ${CampusViewModel.formatTimestamp(exam.examTimestamp)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    if (exam.room.isNotBlank()) {
                        Text(
                            text = "📍 Venue / Hall: ${exam.room}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (exam.seatNumber.isNotBlank()) {
                        Text(
                            text = "💺 Seat / Desk: ${exam.seatNumber}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (exam.topics.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Syllabus: ${exam.topics}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditExamDialog(
    initial: Exam?,
    onDismiss: () -> Unit,
    onSave: (String, Long, String, String, String) -> Unit
) {
    var subject by remember { mutableStateOf(initial?.subject ?: "") }
    var daysAhead by remember { mutableIntStateOf(5) }
    var room by remember { mutableStateOf(initial?.room ?: "Science Hall 101") }
    var topics by remember { mutableStateOf(initial?.topics ?: "") }
    var seatNumber by remember { mutableStateOf(initial?.seatNumber ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial != null) "Edit Exam" else "Schedule New Exam") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Exam / Course Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("exam_subject_input")
                )

                Text("Exam in (Days from now):", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val daysOptions = listOf(1, 2, 3, 5, 7, 10, 14, 21, 30)
                    items(daysOptions) { d ->
                        FilterChip(
                            selected = daysAhead == d,
                            onClick = { daysAhead = d },
                            label = { Text(if (d == 1) "Tomorrow" else "$d days") }
                        )
                    }
                }

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Hall / Room Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = seatNumber,
                    onValueChange = { seatNumber = it },
                    label = { Text("Desk / Roll / Seat Number (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = topics,
                    onValueChange = { topics = it },
                    label = { Text("Chapters / Syllabus Topics") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        val timestamp = if (initial != null) {
                            initial.examTimestamp
                        } else {
                            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(daysAhead.toLong()) + TimeUnit.HOURS.toMillis(2)
                        }
                        onSave(subject, timestamp, room, topics, seatNumber)
                    }
                },
                enabled = subject.isNotBlank(),
                modifier = Modifier.testTag("save_exam_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
