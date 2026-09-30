package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AssignmentScreen
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.CampusHubScreen
import com.example.ui.screens.ContactsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExamScreen
import com.example.ui.screens.GpaCalculatorScreen
import com.example.ui.screens.StudyTimerScreen
import com.example.ui.screens.TimetableScreen
import com.example.ui.screens.UserGuideScreen
import com.example.ui.theme.CampusMateTheme
import com.example.ui.viewmodel.CampusViewModel

data class BottomNavItem(
    val title: String,
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

class MainActivity : ComponentActivity() {
    private val viewModel: CampusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusMateTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: CampusViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }

    // Intercept back navigation on sub-screens to return to Dashboard (or Hub if from Hub)
    BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
        currentScreen = if (currentScreen in listOf(
                Screen.EXAMS,
                Screen.STUDY_TIMER,
                Screen.GPA_CALCULATOR,
                Screen.CONTACTS,
                Screen.USER_GUIDE
            )
        ) {
            Screen.CAMPUS_HUB
        } else {
            Screen.DASHBOARD
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Home",
            screen = Screen.DASHBOARD,
            selectedIcon = Icons.Filled.Dashboard,
            unselectedIcon = Icons.Outlined.Dashboard,
            tag = "nav_home"
        ),
        BottomNavItem(
            title = "Timetable",
            screen = Screen.TIMETABLE,
            selectedIcon = Icons.Filled.CalendarMonth,
            unselectedIcon = Icons.Outlined.CalendarMonth,
            tag = "nav_timetable"
        ),
        BottomNavItem(
            title = "Attendance",
            screen = Screen.ATTENDANCE,
            selectedIcon = Icons.Filled.BarChart,
            unselectedIcon = Icons.Outlined.BarChart,
            tag = "nav_attendance"
        ),
        BottomNavItem(
            title = "Tasks",
            screen = Screen.ASSIGNMENTS,
            selectedIcon = Icons.Filled.Assignment,
            unselectedIcon = Icons.Outlined.Assignment,
            tag = "nav_assignments"
        ),
        BottomNavItem(
            title = "Campus Hub",
            screen = Screen.CAMPUS_HUB,
            selectedIcon = Icons.Filled.Widgets,
            unselectedIcon = Icons.Outlined.Widgets,
            tag = "nav_hub"
        )
    )

    val isHubActive = currentScreen in listOf(
        Screen.CAMPUS_HUB,
        Screen.EXAMS,
        Screen.STUDY_TIMER,
        Screen.GPA_CALCULATOR,
        Screen.CONTACTS,
        Screen.USER_GUIDE
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = when (currentScreen) {
                    Screen.DASHBOARD -> "CampusMate"
                    Screen.TIMETABLE -> "Class Timetable"
                    Screen.ATTENDANCE -> "Attendance Tracker"
                    Screen.ASSIGNMENTS -> "Assignments"
                    Screen.CAMPUS_HUB -> "Campus Hub"
                    Screen.EXAMS -> "Exam Countdown"
                    Screen.STUDY_TIMER -> "Study Timer"
                    Screen.GPA_CALCULATOR -> "GPA Calculator"
                    Screen.CONTACTS -> "Campus Contacts"
                    Screen.USER_GUIDE -> "Step-by-Step Guide"
                },
                onHelpClick = { currentScreen = Screen.USER_GUIDE },
                onEmergencyClick = { currentScreen = Screen.CONTACTS }
            )
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.testTag("bottom_nav_bar")) {
                bottomNavItems.forEach { item ->
                    val isSelected = when (item.screen) {
                        Screen.DASHBOARD -> currentScreen == Screen.DASHBOARD
                        Screen.TIMETABLE -> currentScreen == Screen.TIMETABLE
                        Screen.ATTENDANCE -> currentScreen == Screen.ATTENDANCE
                        Screen.ASSIGNMENTS -> currentScreen == Screen.ASSIGNMENTS
                        Screen.CAMPUS_HUB -> isHubActive
                        else -> currentScreen == item.screen
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = item.screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    Screen.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { target -> currentScreen = target }
                    )
                    Screen.TIMETABLE -> TimetableScreen(viewModel = viewModel)
                    Screen.ATTENDANCE -> AttendanceScreen(viewModel = viewModel)
                    Screen.ASSIGNMENTS -> AssignmentScreen(viewModel = viewModel)
                    Screen.CAMPUS_HUB -> CampusHubScreen(
                        onNavigate = { target -> currentScreen = target }
                    )
                    Screen.EXAMS -> ExamScreen(viewModel = viewModel)
                    Screen.STUDY_TIMER -> StudyTimerScreen(viewModel = viewModel)
                    Screen.GPA_CALCULATOR -> GpaCalculatorScreen(viewModel = viewModel)
                    Screen.CONTACTS -> ContactsScreen(viewModel = viewModel)
                    Screen.USER_GUIDE -> UserGuideScreen(
                        onNavigate = { target -> currentScreen = target }
                    )
                }
            }
        }
    }
}
