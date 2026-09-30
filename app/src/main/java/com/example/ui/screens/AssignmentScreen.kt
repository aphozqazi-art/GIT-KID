package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.model.Assignment
import com.example.data.model.AssignmentPriority
import com.example.ui.components.EmptyStateView
import com.example.ui.viewmodel.CampusViewModel
import java.util.concurrent.TimeUnit

enum class AssignmentFilter {
    ALL,
    PENDING,
    COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    viewModel: CampusViewModel
) {
    val assignments by viewModel.assignments.collectAsState()
    var selectedFilter by remember { mutableStateOf(AssignmentFilter.ALL) }

    var showAddDialog by remember { mutableStateOf(false) }
    var assignmentToEdit by remember { mutableStateOf<Assignment?>(null) }
    var assignmentToDelete by remember { mutableStateOf<Assignment?>(null) }

    val filteredList = when (selectedFilter) {
        AssignmentFilter.ALL -> assignments
        AssignmentFilter.PENDING -> assignments.filter { !it.isCompleted }
        AssignmentFilter.COMPLETED -> assignments.filter { it.isCompleted }
    }

    val pendingCount = assignments.count { !it.isCompleted }
    val completedCount = assignments.count { it.isCompleted }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_assignment_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Assignment")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("assignment_screen")
        ) {
            // Filters Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == AssignmentFilter.ALL,
                        onClick = { selectedFilter = AssignmentFilter.ALL },
                        label = { Text("All (${assignments.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AssignmentFilter.PENDING,
                        onClick = { selectedFilter = AssignmentFilter.PENDING },
                        label = { Text("Pending ($pendingCount)") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AssignmentFilter.COMPLETED,
                        onClick = { selectedFilter = AssignmentFilter.COMPLETED },
                        label = { Text("Completed ($completedCount)") }
                    )
                }
            }

            if (filteredList.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Assignment,
                    title = if (selectedFilter == AssignmentFilter.COMPLETED) "No Completed Tasks" else "No Assignments Found",
                    description = if (selectedFilter == AssignmentFilter.COMPLETED)
                        "Complete assignments by ticking the checkbox on the task card."
                    else
                        "You're all caught up! Tap '+' to log a new coursework or project assignment.",
                    actionText = "Add Assignment",
                    onActionClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        AssignmentCard(
                            assignment = item,
                            onToggle = { isChecked ->
                                viewModel.toggleAssignmentCompleted(item.id, isChecked)
                            },
                            onEdit = { assignmentToEdit = item },
                            onDelete = { assignmentToDelete = item }
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
    if (showAddDialog || assignmentToEdit != null) {
        val initial = assignmentToEdit
        AddEditAssignmentDialog(
            initial = initial,
            onDismiss = {
                showAddDialog = false
                assignmentToEdit = null
            },
            onSave = { title, subject, deadlineTimestamp, priority, notes ->
                if (initial != null) {
                    viewModel.updateAssignment(
                        initial.copy(
                            title = title,
                            subject = subject,
                            deadlineTimestamp = deadlineTimestamp,
                            priority = priority,
                            notes = notes
                        )
                    )
                } else {
                    viewModel.addAssignment(title, subject, deadlineTimestamp, priority, notes)
                }
                showAddDialog = false
                assignmentToEdit = null
            }
        )
    }

    // Confirm Delete Dialog
    if (assignmentToDelete != null) {
        AlertDialog(
            onDismissRequest = { assignmentToDelete = null },
            title = { Text("Delete Assignment?") },
            text = { Text("Are you sure you want to delete '${assignmentToDelete?.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        assignmentToDelete?.let { viewModel.deleteAssignment(it) }
                        assignmentToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { assignmentToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AssignmentCard(
    assignment: Assignment,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val now = System.currentTimeMillis()
    val isOverdue = !assignment.isCompleted && assignment.deadlineTimestamp < now
    val diffDays = TimeUnit.MILLISECONDS.toDays(assignment.deadlineTimestamp - now)

    val priorityColor = when (assignment.priority) {
        AssignmentPriority.HIGH -> MaterialTheme.colorScheme.error
        AssignmentPriority.MEDIUM -> MaterialTheme.colorScheme.secondary
        AssignmentPriority.LOW -> MaterialTheme.colorScheme.tertiary
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("assignment_card_${assignment.id}"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = assignment.isCompleted,
                onCheckedChange = onToggle,
                modifier = Modifier.testTag("checkbox_${assignment.id}")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = priorityColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${assignment.priority.name} Priority",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = priorityColor
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (assignment.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (assignment.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = assignment.subject,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )

                if (assignment.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = assignment.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Due: ${CampusViewModel.formatTimestamp(assignment.deadlineTimestamp)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal,
                            color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    if (!assignment.isCompleted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isOverdue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (isOverdue) "Overdue" else if (diffDays == 0L) "Due today" else "in $diffDays days",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isOverdue) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditAssignmentDialog(
    initial: Assignment?,
    onDismiss: () -> Unit,
    onSave: (String, String, Long, AssignmentPriority, String) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var subject by remember { mutableStateOf(initial?.subject ?: "") }
    var daysAhead by remember { mutableIntStateOf(3) }
    var priority by remember { mutableStateOf(initial?.priority ?: AssignmentPriority.MEDIUM) }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial != null) "Edit Assignment" else "New Assignment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Assignment Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("assignment_title_input")
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Course / Subject *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("assignment_subject_input")
                )

                // Due in days quick selector
                Text("Due Date (Days from today):", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val dayOptions = listOf(1, 2, 3, 5, 7, 14, 21)
                    items(dayOptions) { d ->
                        FilterChip(
                            selected = daysAhead == d,
                            onClick = { daysAhead = d },
                            label = { Text(if (d == 1) "Tomorrow" else "in $d days") }
                        )
                    }
                }

                // Priority selector
                Text("Priority Level:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssignmentPriority.entries.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.name) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Submission Link (optional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && subject.isNotBlank()) {
                        val deadline = if (initial != null) {
                            initial.deadlineTimestamp
                        } else {
                            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(daysAhead.toLong())
                        }
                        onSave(title, subject, deadline, priority, notes)
                    }
                },
                enabled = title.isNotBlank() && subject.isNotBlank(),
                modifier = Modifier.testTag("save_assignment_button")
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
