package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AssignmentPriority {
    HIGH,
    MEDIUM,
    LOW
}

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val deadlineTimestamp: Long, // Epoch millis
    val isCompleted: Boolean = false,
    val priority: AssignmentPriority = AssignmentPriority.MEDIUM,
    val notes: String = ""
)
