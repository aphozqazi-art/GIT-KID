package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable_classes")
data class TimetableClass(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val teacher: String,
    val room: String,
    val dayOfWeek: String, // "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    val startTime: String, // e.g. "09:00 AM"
    val endTime: String,   // e.g. "10:30 AM"
    val classType: String = "Lecture", // "Lecture", "Lab", "Tutorial", "Seminar"
    val colorHex: String = "#2563EB"
)
