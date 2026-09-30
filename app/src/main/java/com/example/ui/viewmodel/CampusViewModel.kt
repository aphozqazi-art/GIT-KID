package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CampusDatabase
import com.example.data.model.Assignment
import com.example.data.model.AssignmentPriority
import com.example.data.model.AttendanceRecord
import com.example.data.model.CampusContact
import com.example.data.model.ContactCategory
import com.example.data.model.Exam
import com.example.data.model.GpaRecord
import com.example.data.model.StudySession
import com.example.data.model.TimetableClass
import com.example.data.repository.CampusRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class PomodoroMode(val title: String, val defaultMinutes: Int) {
    STUDY("Study Session", 25),
    SHORT_BREAK("Short Break", 5),
    LONG_BREAK("Long Break", 15)
}

data class SubjectGradeItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val credits: Double = 3.0,
    val gradeLetter: String = "A",
    val gradePoints: Double = 4.0
)

val GRADE_OPTIONS = listOf(
    "A+" to 4.0,
    "A" to 4.0,
    "A-" to 3.7,
    "B+" to 3.3,
    "B" to 3.0,
    "B-" to 2.7,
    "C+" to 2.3,
    "C" to 2.0,
    "C-" to 1.7,
    "D+" to 1.3,
    "D" to 1.0,
    "F" to 0.0
)

class CampusViewModel(application: Application) : AndroidViewModel(application) {
    private val database = CampusDatabase.getDatabase(application, viewModelScope)
    private val repository = CampusRepository(
        timetableDao = database.timetableDao(),
        attendanceDao = database.attendanceDao(),
        assignmentDao = database.assignmentDao(),
        examDao = database.examDao(),
        contactDao = database.contactDao(),
        gpaDao = database.gpaDao(),
        studySessionDao = database.studySessionDao()
    )

    // Current Day detection
    val currentDayOfWeek: String = getTodayDayOfWeek()

    // 1. Timetable State
    val timetableClasses: StateFlow<List<TimetableClass>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDay = MutableStateFlow(currentDayOfWeek)
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
    }

    fun addClass(
        subject: String,
        teacher: String,
        room: String,
        dayOfWeek: String,
        startTime: String,
        endTime: String,
        classType: String,
        colorHex: String
    ) {
        viewModelScope.launch {
            repository.insertClass(
                TimetableClass(
                    subject = subject,
                    teacher = teacher,
                    room = room,
                    dayOfWeek = dayOfWeek,
                    startTime = startTime,
                    endTime = endTime,
                    classType = classType,
                    colorHex = colorHex
                )
            )
        }
    }

    fun updateClass(c: TimetableClass) {
        viewModelScope.launch { repository.updateClass(c) }
    }

    fun deleteClass(c: TimetableClass) {
        viewModelScope.launch { repository.deleteClass(c) }
    }

    // 2. Attendance State
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addAttendanceRecord(
        subject: String,
        total: Int,
        attended: Int,
        missed: Int,
        target: Int
    ) {
        viewModelScope.launch {
            repository.insertAttendance(
                AttendanceRecord(
                    subject = subject,
                    totalClasses = total,
                    attendedClasses = attended,
                    missedClasses = missed,
                    targetPercentage = target
                )
            )
        }
    }

    fun updateAttendanceRecord(record: AttendanceRecord) {
        viewModelScope.launch { repository.updateAttendance(record) }
    }

    fun deleteAttendanceRecord(record: AttendanceRecord) {
        viewModelScope.launch { repository.deleteAttendance(record) }
    }

    fun markAttended(id: Long) {
        viewModelScope.launch { repository.markAttended(id) }
    }

    fun markMissed(id: Long) {
        viewModelScope.launch { repository.markMissed(id) }
    }

    fun quickAdjustAttendance(record: AttendanceRecord, attendedDelta: Int, missedDelta: Int) {
        val newAttended = (record.attendedClasses + attendedDelta).coerceAtLeast(0)
        val newMissed = (record.missedClasses + missedDelta).coerceAtLeast(0)
        val newTotal = newAttended + newMissed
        val updated = record.copy(
            totalClasses = newTotal,
            attendedClasses = newAttended,
            missedClasses = newMissed
        )
        viewModelScope.launch { repository.updateAttendance(updated) }
    }

    // 3. Assignments State
    val assignments: StateFlow<List<Assignment>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addAssignment(
        title: String,
        subject: String,
        deadlineTimestamp: Long,
        priority: AssignmentPriority,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertAssignment(
                Assignment(
                    title = title,
                    subject = subject,
                    deadlineTimestamp = deadlineTimestamp,
                    priority = priority,
                    notes = notes
                )
            )
        }
    }

    fun updateAssignment(assignment: Assignment) {
        viewModelScope.launch { repository.updateAssignment(assignment) }
    }

    fun toggleAssignmentCompleted(id: Long, completed: Boolean) {
        viewModelScope.launch { repository.toggleAssignmentCompleted(id, completed) }
    }

    fun deleteAssignment(assignment: Assignment) {
        viewModelScope.launch { repository.deleteAssignment(assignment) }
    }

    // 4. Exams State
    val exams: StateFlow<List<Exam>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addExam(
        subject: String,
        examTimestamp: Long,
        room: String,
        topics: String,
        seatNumber: String
    ) {
        viewModelScope.launch {
            repository.insertExam(
                Exam(
                    subject = subject,
                    examTimestamp = examTimestamp,
                    room = room,
                    topics = topics,
                    seatNumber = seatNumber
                )
            )
        }
    }

    fun updateExam(exam: Exam) {
        viewModelScope.launch { repository.updateExam(exam) }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch { repository.deleteExam(exam) }
    }

    // 5. Contacts State
    val contacts: StateFlow<List<CampusContact>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addContact(
        name: String,
        department: String,
        role: String,
        phone: String,
        email: String,
        location: String,
        category: ContactCategory,
        isEmergency: Boolean
    ) {
        viewModelScope.launch {
            repository.insertContact(
                CampusContact(
                    name = name,
                    department = department,
                    role = role,
                    phone = phone,
                    email = email,
                    location = location,
                    category = category,
                    isEmergency = isEmergency
                )
            )
        }
    }

    fun updateContact(contact: CampusContact) {
        viewModelScope.launch { repository.updateContact(contact) }
    }

    fun deleteContact(contact: CampusContact) {
        viewModelScope.launch { repository.deleteContact(contact) }
    }

    // 6. GPA Calculator State
    val gpaRecords: StateFlow<List<GpaRecord>> = repository.allGpaRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _calculatorSubjects = MutableStateFlow(
        listOf(
            SubjectGradeItem(name = "Data Structures & Algorithms", credits = 4.0, gradeLetter = "A", gradePoints = 4.0),
            SubjectGradeItem(name = "Computer Networks", credits = 3.0, gradeLetter = "A-", gradePoints = 3.7),
            SubjectGradeItem(name = "Linear Algebra", credits = 3.0, gradeLetter = "B+", gradePoints = 3.3),
            SubjectGradeItem(name = "Database Systems", credits = 4.0, gradeLetter = "A", gradePoints = 4.0),
            SubjectGradeItem(name = "Software Engineering", credits = 3.0, gradeLetter = "B+", gradePoints = 3.3)
        )
    )
    val calculatorSubjects: StateFlow<List<SubjectGradeItem>> = _calculatorSubjects.asStateFlow()

    private val _previousCgpa = MutableStateFlow("3.65")
    val previousCgpa: StateFlow<String> = _previousCgpa.asStateFlow()

    private val _previousCredits = MutableStateFlow("45.0")
    val previousCredits: StateFlow<String> = _previousCredits.asStateFlow()

    fun updatePreviousCgpa(value: String) {
        _previousCgpa.value = value
    }

    fun updatePreviousCredits(value: String) {
        _previousCredits.value = value
    }

    fun addSubjectToCalculator() {
        val currentList = _calculatorSubjects.value.toMutableList()
        currentList.add(SubjectGradeItem(name = "New Subject", credits = 3.0, gradeLetter = "A", gradePoints = 4.0))
        _calculatorSubjects.value = currentList
    }

    fun removeSubjectFromCalculator(id: String) {
        _calculatorSubjects.value = _calculatorSubjects.value.filterNot { it.id == id }
    }

    fun updateCalculatorSubject(id: String, name: String, credits: Double, gradeLetter: String, points: Double) {
        _calculatorSubjects.value = _calculatorSubjects.value.map {
            if (it.id == id) it.copy(name = name, credits = credits, gradeLetter = gradeLetter, gradePoints = points)
            else it
        }
    }

    fun saveSemesterGpa(semesterName: String, totalCredits: Double, gpa: Double) {
        viewModelScope.launch {
            repository.insertGpaRecord(
                GpaRecord(
                    semesterName = semesterName,
                    totalCredits = totalCredits,
                    gpa = gpa
                )
            )
        }
    }

    fun deleteGpaRecord(record: GpaRecord) {
        viewModelScope.launch { repository.deleteGpaRecord(record) }
    }

    // 7. Study Pomodoro Timer
    private val _pomodoroMode = MutableStateFlow(PomodoroMode.STUDY)
    val pomodoroMode: StateFlow<PomodoroMode> = _pomodoroMode.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(PomodoroMode.STUDY.defaultMinutes * 60)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _pomodoroCount = MutableStateFlow(0)
    val pomodoroCount: StateFlow<Int> = _pomodoroCount.asStateFlow()

    private val _timerSubject = MutableStateFlow("Data Structures")
    val timerSubject: StateFlow<String> = _timerSubject.asStateFlow()

    val studySessions: StateFlow<List<StudySession>> = repository.allStudySessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var timerJob: Job? = null

    fun setTimerSubject(subject: String) {
        _timerSubject.value = subject
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value && _timerSecondsRemaining.value > 0) {
                delay(1000L)
                _timerSecondsRemaining.value = _timerSecondsRemaining.value - 1
            }
            if (_timerSecondsRemaining.value == 0) {
                onTimerFinished()
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _timerSecondsRemaining.value = _pomodoroMode.value.defaultMinutes * 60
    }

    fun switchPomodoroMode(mode: PomodoroMode) {
        pauseTimer()
        _pomodoroMode.value = mode
        _timerSecondsRemaining.value = mode.defaultMinutes * 60
    }

    fun skipTimer() {
        onTimerFinished()
    }

    private fun onTimerFinished() {
        pauseTimer()
        if (_pomodoroMode.value == PomodoroMode.STUDY) {
            val count = _pomodoroCount.value + 1
            _pomodoroCount.value = count
            // Save study session to database
            viewModelScope.launch {
                repository.insertStudySession(
                    StudySession(
                        subject = _timerSubject.value,
                        durationMinutes = PomodoroMode.STUDY.defaultMinutes,
                        notes = "Completed Pomodoro #$count"
                    )
                )
            }
            // Transition to break
            if (count % 4 == 0) {
                switchPomodoroMode(PomodoroMode.LONG_BREAK)
            } else {
                switchPomodoroMode(PomodoroMode.SHORT_BREAK)
            }
        } else {
            // Break finished, back to study
            switchPomodoroMode(PomodoroMode.STUDY)
        }
    }

    companion object {
        fun getTodayDayOfWeek(): String {
            val cal = Calendar.getInstance()
            return when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "Monday"
                Calendar.TUESDAY -> "Tuesday"
                Calendar.WEDNESDAY -> "Wednesday"
                Calendar.THURSDAY -> "Thursday"
                Calendar.FRIDAY -> "Friday"
                Calendar.SATURDAY -> "Saturday"
                Calendar.SUNDAY -> "Sunday"
                else -> "Monday"
            }
        }

        fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        fun formatDateOnly(timestamp: Long): String {
            val sdf = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        fun formatTimeOnly(timestamp: Long): String {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}
