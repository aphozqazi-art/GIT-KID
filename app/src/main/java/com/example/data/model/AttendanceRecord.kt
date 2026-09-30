package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val totalClasses: Int,
    val attendedClasses: Int,
    val missedClasses: Int,
    val targetPercentage: Int = 75
) {
    val percentage: Float
        get() = if (totalClasses > 0) (attendedClasses.toFloat() / totalClasses) * 100f else 0f

    val isBelowTarget: Boolean
        get() = totalClasses > 0 && percentage < targetPercentage

    /**
     * Classes student must attend consecutively to reach the target percentage.
     */
    val classesNeededToReachTarget: Int
        get() {
            if (totalClasses == 0) return 0
            if (percentage >= targetPercentage) return 0
            if (targetPercentage >= 100) {
                return if (attendedClasses == totalClasses) 0 else 999
            }
            val numerator = (targetPercentage * totalClasses) - (100 * attendedClasses)
            val denominator = 100 - targetPercentage
            if (denominator <= 0) return 0
            return max(0, ceil(numerator.toDouble() / denominator).toInt())
        }

    /**
     * Classes student can safely miss and still remain >= target percentage.
     */
    val classesCanSafelyMiss: Int
        get() {
            if (totalClasses == 0 || targetPercentage <= 0) return 0
            if (percentage < targetPercentage) return 0
            val numerator = (100 * attendedClasses) - (targetPercentage * totalClasses)
            val count = floor(numerator.toDouble() / targetPercentage).toInt()
            return max(0, count)
        }
}
