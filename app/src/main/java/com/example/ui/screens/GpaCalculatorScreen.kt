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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.CampusViewModel
import com.example.ui.viewmodel.GRADE_OPTIONS
import com.example.ui.viewmodel.SubjectGradeItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaCalculatorScreen(
    viewModel: CampusViewModel
) {
    val subjects by viewModel.calculatorSubjects.collectAsState()
    val savedRecords by viewModel.gpaRecords.collectAsState()
    val previousCgpaStr by viewModel.previousCgpa.collectAsState()
    val previousCreditsStr by viewModel.previousCredits.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Semester GPA, 1: Cumulative CGPA
    var showSaveDialog by remember { mutableStateOf(false) }

    // Calculate Semester GPA
    val totalCredits = subjects.sumOf { it.credits }
    val totalQualityPoints = subjects.sumOf { it.credits * it.gradePoints }
    val semesterGpa = if (totalCredits > 0) totalQualityPoints / totalCredits else 0.0

    // Calculate Cumulative CGPA
    val prevCgpa = previousCgpaStr.toDoubleOrNull() ?: 0.0
    val prevCredits = previousCreditsStr.toDoubleOrNull() ?: 0.0
    val combinedCredits = prevCredits + totalCredits
    val combinedQualityPoints = (prevCgpa * prevCredits) + totalQualityPoints
    val cumulativeCgpa = if (combinedCredits > 0) combinedQualityPoints / combinedCredits else semesterGpa

    val academicHonors = when {
        semesterGpa >= 3.8 -> "First Class Honors / Summa Cum Laude"
        semesterGpa >= 3.5 -> "Dean's List / Magna Cum Laude"
        semesterGpa >= 3.0 -> "Good Academic Standing"
        semesterGpa >= 2.0 -> "Satisfactory"
        else -> "Academic Warning"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("gpa_calculator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            TabRow(
                selectedTabIndex = activeTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Semester GPA", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Cumulative CGPA", fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Live Calculated Score Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (activeTab == 0) "Estimated Semester GPA" else "Projected Cumulative CGPA",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%.2f".format(if (activeTab == 0) semesterGpa else cumulativeCgpa),
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (semesterGpa >= 3.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "out of 4.00 Scale",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = academicHonors,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total Credits: ${"%.1f".format(totalCredits)} • Quality Points: ${"%.2f".format(totalQualityPoints)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.fillMaxWidth().testTag("save_gpa_button")
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save This Calculation")
                    }
                }
            }
        }

        // Cumulative inputs if activeTab == 1
        if (activeTab == 1) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Prior Academic History",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = previousCgpaStr,
                                onValueChange = { viewModel.updatePreviousCgpa(it) },
                                label = { Text("Previous CGPA") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = previousCreditsStr,
                                onValueChange = { viewModel.updatePreviousCredits(it) },
                                label = { Text("Completed Credits") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                        Text(
                            text = "Combines ${previousCreditsStr.ifBlank { "0" }} previous credits with ${"%.1f".format(totalCredits)} current semester credits for total ${"%.1f".format(combinedCredits)} credits.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Current Semester Subjects List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Semester Courses (${subjects.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                OutlinedButton(onClick = { viewModel.addSubjectToCalculator() }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Course")
                }
            }
        }

        // List of Course Grade Entries
        items(subjects, key = { it.id }) { item ->
            CourseGradeRow(
                item = item,
                onUpdate = { name, credits, letter, points ->
                    viewModel.updateCalculatorSubject(item.id, name, credits, letter, points)
                },
                onDelete = { viewModel.removeSubjectFromCalculator(item.id) }
            )
        }

        // Saved Records Section
        if (savedRecords.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Saved Semester History",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            items(savedRecords, key = { it.id }) { rec ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = rec.semesterName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${"%.1f".format(rec.totalCredits)} credits • ${CampusViewModel.formatDateOnly(rec.timestamp)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Text(
                                    text = "GPA: ${"%.2f".format(rec.gpa)}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            IconButton(onClick = { viewModel.deleteGpaRecord(rec) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showSaveDialog) {
        var semName by remember { mutableStateOf("Fall 2026") }
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Semester GPA") },
            text = {
                OutlinedTextField(
                    value = semName,
                    onValueChange = { semName = it },
                    label = { Text("Semester / Term Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveSemesterGpa(semName, totalCredits, semesterGpa)
                        showSaveDialog = false
                    }
                ) {
                    Text("Save Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseGradeRow(
    item: SubjectGradeItem,
    onUpdate: (String, Double, String, Double) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = item.name,
                onValueChange = { onUpdate(it, item.credits, item.gradeLetter, item.gradePoints) },
                label = { Text("Course Name") },
                modifier = Modifier.weight(1.5f),
                singleLine = true
            )

            // Credit hours input
            OutlinedTextField(
                value = if (item.credits % 1.0 == 0.0) item.credits.toInt().toString() else item.credits.toString(),
                onValueChange = {
                    val cr = it.toDoubleOrNull() ?: 1.0
                    onUpdate(item.name, cr, item.gradeLetter, item.gradePoints)
                },
                label = { Text("Credits") },
                modifier = Modifier.weight(0.9f),
                singleLine = true
            )

            // Grade dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.weight(0.9f)
            ) {
                OutlinedTextField(
                    value = item.gradeLetter,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Grade") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    GRADE_OPTIONS.forEach { (letter, pts) ->
                        DropdownMenuItem(
                            text = { Text("$letter ($pts)") },
                            onClick = {
                                onUpdate(item.name, item.credits, letter, pts)
                                expanded = false
                            }
                        )
                    }
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
