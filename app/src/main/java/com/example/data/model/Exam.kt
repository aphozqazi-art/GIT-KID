package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val examTimestamp: Long, // Epoch millis
    val room: String = "",
    val topics: String = "",
    val seatNumber: String = ""
) {
    fun daysRemaining(now: Long = System.currentTimeMillis()): Long {
        val diff = examTimestamp - now
        return if (diff < 0) {
            -1L // Already occurred
        } else {
            TimeUnit.MILLISECONDS.toDays(diff)
        }
    }

    fun remainingText(now: Long = System.currentTimeMillis()): String {
        val diff = examTimestamp - now
        if (diff < 0) {
            val daysAgo = TimeUnit.MILLISECONDS.toDays(-diff)
            return if (daysAgo == 0L) "Finished today" else "$daysAgo days ago"
        }
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
        return when {
            days == 0L && hours == 0L -> "Starting soon!"
            days == 0L -> "Today in ${hours}h"
            days == 1L -> "Tomorrow"
            else -> "$days days left"
        }
    }
}
