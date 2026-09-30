package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.ui.components.EmptyStateView
import com.example.ui.viewmodel.CampusViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: CampusViewModel
) {
    val records by viewModel.attendanceRecords.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var recordToEdit by remember { mutableStateOf<AttendanceRecord?>(null) }
    var recordToDelete by remember { mutableStateOf<AttendanceRecord?>(null) }

    val totalHeld = records.sumOf { it.totalClasses }
    val totalAttended = records.sumOf { it.attendedClasses }
    val overallPercentage = if (totalHeld > 0) (totalAttended.toFloat() / totalHeld) * 100f else 0f
    val lowAttendanceCount = records.count { it.isBelowTarget }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_attendance_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Course")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .testTag("attendance_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Overall Summary Header Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "University Attendance Status",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${records.size} Enrolled Courses • $totalAttended of $totalHeld classes attended",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            // Big Percentage Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (overallPercentage >= 75f) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
                                contentColor = if (overallPercentage >= 75f) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onErrorContainer
                            ) {
                                Text(
                                    text = "${"%.1f".format(overallPercentage)}%",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        val animatedProgress by animateFloatAsState(
                            targetValue = (overallPercentage / 100f).coerceIn(0f, 1f),
                            label = "overallProgress"
                        )
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = if (overallPercentage >= 75f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        if (lowAttendanceCount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$lowAttendanceCount course(s) require immediate attendance to prevent exam debarment.",
                                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.error)
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "All courses satisfy university minimum attendance criteria.",
                                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.tertiary)
                                )
                            }
                        }
                    }
                }
            }

            if (records.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.BarChart,
                        title = "No Courses Added Yet",
                        description = "Add your enrolled subjects and log classes to track your attendance percentage and target goals.",
                        actionText = "Add Course",
                        onActionClick = { showAddDialog = true }
                    )
                }
            } else {
                items(records, key = { it.id }) { record ->
                    AttendanceCard(
                        record = record,
                        onPresent = { viewModel.quickAdjustAttendance(record, attendedDelta = 1, missedDelta = 0) },
                        onAbsent = { viewModel.quickAdjustAttendance(record, attendedDelta = 0, missedDelta = 1) },
                        onEdit = { recordToEdit = record },
                        onDelete = { recordToDelete = record }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Add or Edit Dialog
    if (showAddDialog || recordToEdit != null) {
        val editing = recordToEdit
        AddEditAttendanceDialog(
            initial = editing,
            onDismiss = {
                showAddDialog = false
                recordToEdit = null
            },
            onSave = { subject, total, attended, missed, target ->
                if (editing != null) {
                    viewModel.updateAttendanceRecord(
                        editing.copy(
                            subject = subject,
                            totalClasses = total,
                            attendedClasses = attended,
                            missedClasses = missed,
                            targetPercentage = target
                        )
                    )
                } else {
                    viewModel.addAttendanceRecord(subject, total, attended, missed, target)
                }
                showAddDialog = false
                recordToEdit = null
            }
        )
    }

    // Confirm Delete Dialog
    if (recordToDelete != null) {
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            title = { Text("Delete Attendance Record?") },
            text = { Text("Are you sure you want to delete '${recordToDelete?.subject}' attendance record?") },
            confirmButton = {
                Button(
                    onClick = {
                        recordToDelete?.let { viewModel.deleteAttendanceRecord(it) }
                        recordToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AttendanceCard(
    record: AttendanceRecord,
    onPresent: () -> Unit,
    onAbsent: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val pct = record.percentage
    val isSafe = pct >= record.targetPercentage
    val statusColor = if (isSafe) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attendance_card_${record.id}"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Subject title and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.subject,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
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

            // Percentage & Breakdown Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${"%.1f".format(pct)}%",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = statusColor
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Target: ${record.targetPercentage}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Attended: ${record.attendedClasses} • Missed: ${record.missedClasses} • Total: ${record.totalClasses}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isSafe) "On Track" else "Low Attendance",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            val animatedProgress by animateFloatAsState(
                targetValue = (pct / 100f).coerceIn(0f, 1f),
                label = "subjectProgress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Smart Target Recommendation Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (record.isBelowTarget) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Must attend next ${record.classesNeededToReachTarget} consecutive classes to reach ${record.targetPercentage}% target!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val canMiss = record.classesCanSafelyMiss
                        Text(
                            text = if (canMiss > 0)
                                "You can safely miss $canMiss class(es) and remain above ${record.targetPercentage}%."
                            else
                                "Right on target! Attend the next class to build a safety buffer.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Log Buttons (+1 Present, +1 Absent)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
                    onClick = onPresent,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Attended")
                }
                OutlinedButton(
                    onClick = onAbsent,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Missed")
                }
            }
        }
    }
}

@Composable
fun AddEditAttendanceDialog(
    initial: AttendanceRecord?,
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int, Int) -> Unit
) {
    var subject by remember { mutableStateOf(initial?.subject ?: "") }
    var attendedText by remember { mutableStateOf(initial?.attendedClasses?.toString() ?: "15") }
    var missedText by remember { mutableStateOf(initial?.missedClasses?.toString() ?: "3") }
    var targetPercentage by remember { mutableIntStateOf(initial?.targetPercentage ?: 75) }

    val attended = attendedText.toIntOrNull() ?: 0
    val missed = missedText.toIntOrNull() ?: 0
    val total = attended + missed
    val calculatedPct = if (total > 0) (attended.toFloat() / total) * 100f else 0f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial != null) "Edit Course Attendance" else "Add Course Attendance") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Course / Subject Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("subject_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = attendedText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) attendedText = it },
                        label = { Text("Classes Attended") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = missedText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) missedText = it },
                        label = { Text("Classes Missed") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Calculated stats preview
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Total Classes: $total",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Calculated Attendance: ${"%.1f".format(calculatedPct)}%",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (calculatedPct >= targetPercentage) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }

                // Target percentage slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Target Attendance %", style = MaterialTheme.typography.labelMedium)
                        Text("$targetPercentage%", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = targetPercentage.toFloat(),
                        onValueChange = { targetPercentage = it.roundToInt() },
                        valueRange = 50f..95f,
                        steps = 8
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        onSave(subject, total, attended, missed, targetPercentage)
                    }
                },
                enabled = subject.isNotBlank(),
                modifier = Modifier.testTag("save_attendance_button")
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
