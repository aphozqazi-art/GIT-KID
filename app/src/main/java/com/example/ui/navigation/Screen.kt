package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Emergency
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    TIMETABLE("Timetable", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    ATTENDANCE("Attendance", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    ASSIGNMENTS("Assignments", Icons.Filled.Assignment, Icons.Outlined.Assignment),
    CAMPUS_HUB("Campus Hub", Icons.Filled.Widgets, Icons.Outlined.Widgets),
    EXAMS("Exams", Icons.Filled.HourglassTop, Icons.Outlined.HourglassTop),
    STUDY_TIMER("Study Timer", Icons.Filled.Timer, Icons.Outlined.Timer),
    GPA_CALCULATOR("GPA Calc", Icons.Filled.Calculate, Icons.Outlined.Calculate),
    CONTACTS("Contacts", Icons.Filled.Emergency, Icons.Outlined.Emergency),
    USER_GUIDE("User Guide", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
}
