package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Assignment
import com.example.data.model.AssignmentPriority
import com.example.data.model.AttendanceRecord
import com.example.data.model.CampusContact
import com.example.data.model.ContactCategory
import com.example.data.model.Exam
import com.example.data.model.GpaRecord
import com.example.data.model.StudySession
import com.example.data.model.TimetableClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Database(
    entities = [
        TimetableClass::class,
        AttendanceRecord::class,
        Assignment::class,
        Exam::class,
        CampusContact::class,
        GpaRecord::class,
        StudySession::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun timetableDao(): TimetableDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun examDao(): ExamDao
    abstract fun contactDao(): ContactDao
    abstract fun gpaDao(): GpaDao
    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile
        private var INSTANCE: CampusDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CampusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    "campusmate_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: CampusDatabase) {
                // Initial Timetable
                val initialClasses = listOf(
                    TimetableClass(
                        subject = "Data Structures & Algorithms",
                        teacher = "Dr. Alan Turing",
                        room = "Science Hall 302",
                        dayOfWeek = "Monday",
                        startTime = "09:00 AM",
                        endTime = "10:30 AM",
                        classType = "Lecture",
                        colorHex = "#2563EB"
                    ),
                    TimetableClass(
                        subject = "Computer Networks",
                        teacher = "Prof. Grace Hopper",
                        room = "Turing Lab 104",
                        dayOfWeek = "Monday",
                        startTime = "11:00 AM",
                        endTime = "12:30 PM",
                        classType = "Lab",
                        colorHex = "#059669"
                    ),
                    TimetableClass(
                        subject = "Linear Algebra & Calculus",
                        teacher = "Dr. Katherine Johnson",
                        room = "Euler Math Bldg 201",
                        dayOfWeek = "Tuesday",
                        startTime = "10:00 AM",
                        endTime = "11:30 AM",
                        classType = "Lecture",
                        colorHex = "#7C3AED"
                    ),
                    TimetableClass(
                        subject = "Database Systems",
                        teacher = "Dr. Edgar Codd",
                        room = "Science Hall 205",
                        dayOfWeek = "Tuesday",
                        startTime = "01:30 PM",
                        endTime = "03:00 PM",
                        classType = "Lecture",
                        colorHex = "#D97706"
                    ),
                    TimetableClass(
                        subject = "Software Engineering",
                        teacher = "Prof. Margaret Hamilton",
                        room = "Tech Complex 410",
                        dayOfWeek = "Wednesday",
                        startTime = "09:30 AM",
                        endTime = "11:00 AM",
                        classType = "Seminar",
                        colorHex = "#DC2626"
                    ),
                    TimetableClass(
                        subject = "Data Structures Lab",
                        teacher = "Dr. Alan Turing",
                        room = "Computing Lab 2",
                        dayOfWeek = "Wednesday",
                        startTime = "02:00 PM",
                        endTime = "04:00 PM",
                        classType = "Lab",
                        colorHex = "#2563EB"
                    ),
                    TimetableClass(
                        subject = "Computer Networks",
                        teacher = "Prof. Grace Hopper",
                        room = "Science Hall 302",
                        dayOfWeek = "Thursday",
                        startTime = "09:00 AM",
                        endTime = "10:30 AM",
                        classType = "Lecture",
                        colorHex = "#059669"
                    ),
                    TimetableClass(
                        subject = "Linear Algebra Tutorial",
                        teacher = "Dr. Katherine Johnson",
                        room = "Euler Math Bldg 105",
                        dayOfWeek = "Thursday",
                        startTime = "11:30 AM",
                        endTime = "01:00 PM",
                        classType = "Tutorial",
                        colorHex = "#7C3AED"
                    ),
                    TimetableClass(
                        subject = "Database Systems Lab",
                        teacher = "Dr. Edgar Codd",
                        room = "Turing Lab 102",
                        dayOfWeek = "Friday",
                        startTime = "10:00 AM",
                        endTime = "12:00 PM",
                        classType = "Lab",
                        colorHex = "#D97706"
                    ),
                    TimetableClass(
                        subject = "Software Engineering Project",
                        teacher = "Prof. Margaret Hamilton",
                        room = "Tech Complex 410",
                        dayOfWeek = "Friday",
                        startTime = "02:00 PM",
                        endTime = "03:30 PM",
                        classType = "Lecture",
                        colorHex = "#DC2626"
                    )
                )
                database.timetableDao().insertAll(initialClasses)

                // Initial Attendance Records
                val initialAttendance = listOf(
                    AttendanceRecord(
                        subject = "Data Structures & Algorithms",
                        totalClasses = 22,
                        attendedClasses = 15,
                        missedClasses = 7,
                        targetPercentage = 75
                    ),
                    AttendanceRecord(
                        subject = "Computer Networks",
                        totalClasses = 20,
                        attendedClasses = 18,
                        missedClasses = 2,
                        targetPercentage = 75
                    ),
                    AttendanceRecord(
                        subject = "Linear Algebra & Calculus",
                        totalClasses = 18,
                        attendedClasses = 16,
                        missedClasses = 2,
                        targetPercentage = 75
                    ),
                    AttendanceRecord(
                        subject = "Database Systems",
                        totalClasses = 20,
                        attendedClasses = 16,
                        missedClasses = 4,
                        targetPercentage = 80
                    ),
                    AttendanceRecord(
                        subject = "Software Engineering",
                        totalClasses = 19,
                        attendedClasses = 17,
                        missedClasses = 2,
                        targetPercentage = 75
                    )
                )
                database.attendanceDao().insertAll(initialAttendance)

                // Initial Assignments (relative to now)
                val now = System.currentTimeMillis()
                val dayMillis = TimeUnit.DAYS.toMillis(1)
                val initialAssignments = listOf(
                    Assignment(
                        title = "Binary Search Tree Implementation",
                        subject = "Data Structures & Algorithms",
                        deadlineTimestamp = now + (2 * dayMillis) + TimeUnit.HOURS.toMillis(4),
                        isCompleted = false,
                        priority = AssignmentPriority.HIGH,
                        notes = "Include deletion algorithm and balancing analysis"
                    ),
                    Assignment(
                        title = "Wireshark Packet Analysis Report",
                        subject = "Computer Networks",
                        deadlineTimestamp = now + (5 * dayMillis),
                        isCompleted = false,
                        priority = AssignmentPriority.MEDIUM,
                        notes = "Capture TCP handshake and DNS resolution traffic"
                    ),
                    Assignment(
                        title = "E-Commerce ER Diagram & SQL Schema",
                        subject = "Database Systems",
                        deadlineTimestamp = now + (8 * dayMillis),
                        isCompleted = false,
                        priority = AssignmentPriority.MEDIUM,
                        notes = "Design 3NF normalized schema with foreign keys"
                    ),
                    Assignment(
                        title = "Eigenvalues & Matrix Transformations",
                        subject = "Linear Algebra & Calculus",
                        deadlineTimestamp = now - dayMillis,
                        isCompleted = true,
                        priority = AssignmentPriority.LOW,
                        notes = "Submitted on university portal"
                    )
                )
                database.assignmentDao().insertAll(initialAssignments)

                // Initial Exams
                val initialExams = listOf(
                    Exam(
                        subject = "Data Structures Midterm",
                        examTimestamp = now + (4 * dayMillis) + TimeUnit.HOURS.toMillis(2),
                        room = "Auditorium Hall B",
                        topics = "Arrays, Linked Lists, Stacks, Queues, Trees, Big-O Complexity",
                        seatNumber = "Desk B-42"
                    ),
                    Exam(
                        subject = "Linear Algebra Quiz 2",
                        examTimestamp = now + (11 * dayMillis),
                        room = "Euler Math Bldg 201",
                        topics = "Vector Spaces, Orthogonality, Determinants",
                        seatNumber = "Desk M-15"
                    ),
                    Exam(
                        subject = "Computer Networks Midterm",
                        examTimestamp = now + (18 * dayMillis),
                        room = "Science Hall 101",
                        topics = "OSI Model, TCP/IP, Routing Protocols, Subnetting",
                        seatNumber = "Desk C-08"
                    )
                )
                database.examDao().insertAll(initialExams)

                // Initial University & Emergency Contacts
                val initialContacts = listOf(
                    CampusContact(
                        name = "Campus Security (24/7 Patrol)",
                        department = "Safety & Emergency Services",
                        role = "Emergency Patrol & Escort",
                        phone = "+1-555-0199",
                        email = "security@campus.edu",
                        location = "Gate 1, Campus Safety HQ",
                        category = ContactCategory.EMERGENCY,
                        isEmergency = true
                    ),
                    CampusContact(
                        name = "University Health & Ambulance",
                        department = "Medical Clinic",
                        role = "Emergency Medical Services",
                        phone = "+1-555-0911",
                        email = "healthcenter@campus.edu",
                        location = "Student Wellness Center, Rm 100",
                        category = ContactCategory.EMERGENCY,
                        isEmergency = true
                    ),
                    CampusContact(
                        name = "Academic Administration",
                        department = "Registrar & Records",
                        role = "Enrollment & Transcripts",
                        phone = "+1-555-0120",
                        email = "registrar@campus.edu",
                        location = "Administration Building, 2nd Fl",
                        category = ContactCategory.ADMINISTRATION,
                        isEmergency = false
                    ),
                    CampusContact(
                        name = "Dean of Student Affairs",
                        department = "Student Affairs Office",
                        role = "Student Welfare & Grievances",
                        phone = "+1-555-0130",
                        email = "dean.students@campus.edu",
                        location = "Student Union, Suite 305",
                        category = ContactCategory.ADMINISTRATION,
                        isEmergency = false
                    ),
                    CampusContact(
                        name = "Computer Science Department",
                        department = "School of Computing",
                        role = "Faculty Advisor & Course Inquiries",
                        phone = "+1-555-0155",
                        email = "cs.office@campus.edu",
                        location = "Turing Computing Complex, Rm 210",
                        category = ContactCategory.ACADEMIC,
                        isEmergency = false
                    ),
                    CampusContact(
                        name = "Campus IT & WiFi Helpdesk",
                        department = "Information Technology",
                        role = "Account & Network Support",
                        phone = "+1-555-0170",
                        email = "ithelpdesk@campus.edu",
                        location = "Central Library, Ground Floor",
                        category = ContactCategory.SUPPORT,
                        isEmergency = false
                    ),
                    CampusContact(
                        name = "University Central Library",
                        department = "Library Services",
                        role = "Circulation & Study Rooms",
                        phone = "+1-555-0180",
                        email = "library@campus.edu",
                        location = "Library Building",
                        category = ContactCategory.SUPPORT,
                        isEmergency = false
                    )
                )
                database.contactDao().insertAll(initialContacts)

                // Initial Study Session
                val initialStudySession = StudySession(
                    subject = "Data Structures",
                    durationMinutes = 25,
                    notes = "Covered AVL tree rotations"
                )
                database.studySessionDao().insertSession(initialStudySession)
            }
        }
    }
}
