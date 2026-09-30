package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records ORDER BY subject ASC")
    fun getAllRecords(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE id = :id")
    suspend fun getRecordById(id: Long): AttendanceRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<AttendanceRecord>)

    @Update
    suspend fun updateRecord(record: AttendanceRecord)

    @Delete
    suspend fun deleteRecord(record: AttendanceRecord)

    @Query("DELETE FROM attendance_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
