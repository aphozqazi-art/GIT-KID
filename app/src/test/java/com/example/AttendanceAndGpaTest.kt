package com.example

import com.example.data.model.AttendanceRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AttendanceAndGpaTest {

    @Test
    fun testAttendancePercentage() {
        val record = AttendanceRecord(
            subject = "Data Structures",
            totalClasses = 20,
            attendedClasses = 15,
            missedClasses = 5,
            targetPercentage = 75
        )
        assertEquals(75.0f, record.percentage, 0.01f)
        assertEquals(0, record.classesNeededToReachTarget)
        assertEquals(0, record.classesCanSafelyMiss)
    }

    @Test
    fun testAttendanceBelowTargetNeedsClasses() {
        // 10 attended out of 20 = 50%. Target is 75%.
        // Formula: ceil((75 * 20 - 100 * 10) / (100 - 75)) = ceil((1500 - 1000) / 25) = 500 / 25 = 20 classes.
        // If student attends 20 more: 30 / 40 = 75%.
        val record = AttendanceRecord(
            subject = "Math",
            totalClasses = 20,
            attendedClasses = 10,
            missedClasses = 10,
            targetPercentage = 75
        )
        assertEquals(50.0f, record.percentage, 0.01f)
        assertTrue(record.isBelowTarget)
        assertEquals(20, record.classesNeededToReachTarget)
    }

    @Test
    fun testAttendanceAboveTargetCanMissClasses() {
        // 18 attended out of 20 = 90%. Target is 75%.
        // Formula: floor((100 * 18 - 75 * 20) / 75) = floor((1800 - 1500) / 75) = floor(300 / 75) = 4 classes.
        // If student misses 4: 18 / 24 = 75%.
        val record = AttendanceRecord(
            subject = "Networks",
            totalClasses = 20,
            attendedClasses = 18,
            missedClasses = 2,
            targetPercentage = 75
        )
        assertEquals(90.0f, record.percentage, 0.01f)
        assertEquals(0, record.classesNeededToReachTarget)
        assertEquals(4, record.classesCanSafelyMiss)
    }
}
