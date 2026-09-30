package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gpa_records")
data class GpaRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val semesterName: String, // e.g. "Fall 2026", "Semester 3"
    val totalCredits: Double,
    val gpa: Double,
    val timestamp: Long = System.currentTimeMillis()
)
