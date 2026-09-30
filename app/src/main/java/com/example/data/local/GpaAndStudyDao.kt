package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.GpaRecord
import com.example.data.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface GpaDao {
    @Query("SELECT * FROM gpa_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<GpaRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GpaRecord): Long

    @Delete
    suspend fun deleteRecord(record: GpaRecord)
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long

    @Query("SELECT SUM(durationMinutes) FROM study_sessions")
    fun getTotalMinutesStudied(): Flow<Int?>
}
