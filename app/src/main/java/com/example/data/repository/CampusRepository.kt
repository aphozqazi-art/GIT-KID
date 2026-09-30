package com.example.data.repository

import com.example.data.local.AssignmentDao
import com.example.data.local.AttendanceDao
import com.example.data.local.ContactDao
import com.example.data.local.ExamDao
import com.example.data.local.GpaDao
import com.example.data.local.StudySessionDao
import com.example.data.local.TimetableDao
import com.example.data.model.Assignment
import com.example.data.model.AttendanceRecord
import com.example.data.model.CampusContact
import com.example.data.model.Exam
import com.example.data.model.GpaRecord
import com.example.data.model.StudySession
import com.example.data.model.TimetableClass
import kotlinx.coroutines.flow.Flow

class CampusRepository(
    private val timetableDao: TimetableDao,
    private val attendanceDao: AttendanceDao,
    private val assignmentDao: AssignmentDao,
    private val examDao: ExamDao,
    private val contactDao: ContactDao,
    private val gpaDao: GpaDao,
    private val studySessionDao: StudySessionDao
) {
    // Timetable
    val allClasses: Flow<List<TimetableClass>> = timetableDao.getAllClasses()
    fun getClassesForDay(day: String): Flow<List<TimetableClass>> = timetableDao.getClassesForDay(day)
    suspend fun insertClass(c: TimetableClass) = timetableDao.insertClass(c)
    suspend fun updateClass(c: TimetableClass) = timetableDao.updateClass(c)
    suspend fun deleteClass(c: TimetableClass) = timetableDao.deleteClass(c)
    suspend fun deleteClassById(id: Long) = timetableDao.deleteById(id)

    // Attendance
    val allAttendance: Flow<List<AttendanceRecord>> = attendanceDao.getAllRecords()
    suspend fun insertAttendance(a: AttendanceRecord) = attendanceDao.insertRecord(a)
    suspend fun updateAttendance(a: AttendanceRecord) = attendanceDao.updateRecord(a)
    suspend fun deleteAttendance(a: AttendanceRecord) = attendanceDao.deleteRecord(a)
    suspend fun deleteAttendanceById(id: Long) = attendanceDao.deleteById(id)

    suspend fun markAttended(id: Long) {
        val record = attendanceDao.getRecordById(id) ?: return
        val updated = record.copy(
            totalClasses = record.totalClasses + 1,
            attendedClasses = record.attendedClasses + 1
        )
        attendanceDao.updateRecord(updated)
    }

    suspend fun markMissed(id: Long) {
        val record = attendanceDao.getRecordById(id) ?: return
        val updated = record.copy(
            totalClasses = record.totalClasses + 1,
            missedClasses = record.missedClasses + 1
        )
        attendanceDao.updateRecord(updated)
    }

    // Assignments
    val allAssignments: Flow<List<Assignment>> = assignmentDao.getAllAssignments()
    suspend fun insertAssignment(a: Assignment) = assignmentDao.insertAssignment(a)
    suspend fun updateAssignment(a: Assignment) = assignmentDao.updateAssignment(a)
    suspend fun deleteAssignment(a: Assignment) = assignmentDao.deleteAssignment(a)
    suspend fun deleteAssignmentById(id: Long) = assignmentDao.deleteById(id)
    suspend fun toggleAssignmentCompleted(id: Long, completed: Boolean) = assignmentDao.toggleCompleted(id, completed)

    // Exams
    val allExams: Flow<List<Exam>> = examDao.getAllExams()
    suspend fun insertExam(e: Exam) = examDao.insertExam(e)
    suspend fun updateExam(e: Exam) = examDao.updateExam(e)
    suspend fun deleteExam(e: Exam) = examDao.deleteExam(e)
    suspend fun deleteExamById(id: Long) = examDao.deleteById(id)

    // Contacts
    val allContacts: Flow<List<CampusContact>> = contactDao.getAllContacts()
    suspend fun insertContact(c: CampusContact) = contactDao.insertContact(c)
    suspend fun updateContact(c: CampusContact) = contactDao.updateContact(c)
    suspend fun deleteContact(c: CampusContact) = contactDao.deleteContact(c)
    suspend fun deleteContactById(id: Long) = contactDao.deleteById(id)

    // GPA
    val allGpaRecords: Flow<List<GpaRecord>> = gpaDao.getAllRecords()
    suspend fun insertGpaRecord(g: GpaRecord) = gpaDao.insertRecord(g)
    suspend fun deleteGpaRecord(g: GpaRecord) = gpaDao.deleteRecord(g)

    // Study
    val allStudySessions: Flow<List<StudySession>> = studySessionDao.getAllSessions()
    val totalMinutesStudied: Flow<Int?> = studySessionDao.getTotalMinutesStudied()
    suspend fun insertStudySession(s: StudySession) = studySessionDao.insertSession(s)
}
