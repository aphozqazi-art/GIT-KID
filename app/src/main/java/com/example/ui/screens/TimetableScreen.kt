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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.TimetableClass
import com.example.ui.components.EmptyStateView
import com.example.ui.viewmodel.CampusViewModel

val DAYS_OF_WEEK = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
val CLASS_TYPES = listOf("Lecture", "Lab", "Tutorial", "Seminar", "Workshop")
val COLOR_OPTIONS = listOf("#2563EB", "#059669", "#7C3AED", "#D97706", "#DC2626", "#0891B2")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    viewModel: CampusViewModel
) {
    val allClasses by viewModel.timetableClasses.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var classToEdit by remember { mutableStateOf<TimetableClass?>(null) }
    var classToDelete by remember { mutableStateOf<TimetableClass?>(null) }

    val dayClasses = allClasses.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_class_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Class")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("timetable_screen")
        ) {
            // Day selector tabs
            val selectedIndex = DAYS_OF_WEEK.indexOf(selectedDay).coerceAtLeast(0)
            ScrollableTabRow(
                selectedTabIndex = selectedIndex,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                DAYS_OF_WEEK.forEachIndexed { index, day ->
                    val count = allClasses.count { it.dayOfWeek.equals(day, ignoreCase = true) }
                    Tab(
                        selected = selectedDay == day,
                        onClick = { viewModel.setSelectedDay(day) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(day)
                                if (count > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = if (selectedDay == day) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "$count",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (selectedDay == day) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (dayClasses.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.CalendarMonth,
                    title = "No Classes on $selectedDay",
                    description = "Take a break, review course notes, or tap the '+' button to schedule a new class.",
                    actionText = "Add Class for $selectedDay",
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
                    items(dayClasses, key = { it.id }) { item ->
                        TimetableCard(
                            timetableClass = item,
                            onEdit = { classToEdit = item },
                            onDelete = { classToDelete = item }
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
    if (showAddDialog || classToEdit != null) {
        val initial = classToEdit
        AddEditClassDialog(
            initialClass = initial,
            defaultDay = selectedDay,
            onDismiss = {
                showAddDialog = false
                classToEdit = null
            },
            onSave = { subject, teacher, room, day, start, end, type, color ->
                if (initial != null) {
                    viewModel.updateClass(
                        initial.copy(
                            subject = subject,
                            teacher = teacher,
                            room = room,
                            dayOfWeek = day,
                            startTime = start,
                            endTime = end,
                            classType = type,
                            colorHex = color
                        )
                    )
                } else {
                    viewModel.addClass(subject, teacher, room, day, start, end, type, color)
                }
                showAddDialog = false
                classToEdit = null
            }
        )
    }

    // Confirm Delete Dialog
    if (classToDelete != null) {
        AlertDialog(
            onDismissRequest = { classToDelete = null },
            title = { Text("Delete Class?") },
            text = { Text("Are you sure you want to remove '${classToDelete?.subject}' from $selectedDay?") },
            confirmButton = {
                Button(
                    onClick = {
                        classToDelete?.let { viewModel.deleteClass(it) }
                        classToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { classToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TimetableCard(
    timetableClass: TimetableClass,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val barColor = try {
        Color(android.graphics.Color.parseColor(timetableClass.colorHex))
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("class_card_${timetableClass.id}"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp, 80.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = barColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = timetableClass.classType,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = barColor
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timetableClass.subject,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${timetableClass.startTime} - ${timetableClass.endTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timetableClass.room,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timetableClass.teacher,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClassDialog(
    initialClass: TimetableClass?,
    defaultDay: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String) -> Unit
) {
    var subject by remember { mutableStateOf(initialClass?.subject ?: "") }
    var teacher by remember { mutableStateOf(initialClass?.teacher ?: "") }
    var room by remember { mutableStateOf(initialClass?.room ?: "") }
    var day by remember { mutableStateOf(initialClass?.dayOfWeek ?: defaultDay) }
    var startTime by remember { mutableStateOf(initialClass?.startTime ?: "09:00 AM") }
    var endTime by remember { mutableStateOf(initialClass?.endTime ?: "10:30 AM") }
    var classType by remember { mutableStateOf(initialClass?.classType ?: "Lecture") }
    var selectedColor by remember { mutableStateOf(initialClass?.colorHex ?: "#2563EB") }

    var dayExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialClass != null) "Edit Class" else "Add New Class") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("subject_input")
                )
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Teacher / Professor") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("teacher_input")
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room / Hall Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("room_input")
                )

                // Day of Week Picker
                ExposedDropdownMenuBox(
                    expanded = dayExpanded,
                    onExpandedChange = { dayExpanded = it }
                ) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Day of Week") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = dayExpanded,
                        onDismissRequest = { dayExpanded = false }
                    ) {
                        DAYS_OF_WEEK.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d) },
                                onClick = {
                                    day = d
                                    dayExpanded = false
                                }
                            )
                        }
                    }
                }

                // Timing Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Class Type Picker
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = classType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Class Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        CLASS_TYPES.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    classType = t
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Color accent choices
                Text(
                    text = "Color Tag",
                    style = MaterialTheme.typography.labelMedium
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(COLOR_OPTIONS) { c ->
                        val parsed = Color(android.graphics.Color.parseColor(c))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(parsed)
                                .padding(2.dp)
                                .testTag("color_$c"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == c) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        onSave(subject, teacher, room, day, startTime, endTime, classType, selectedColor)
                    }
                },
                enabled = subject.isNotBlank(),
                modifier = Modifier.testTag("save_class_button")
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
