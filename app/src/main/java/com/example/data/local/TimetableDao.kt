package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TimetableClass
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable_classes ORDER BY startTime ASC")
    fun getAllClasses(): Flow<List<TimetableClass>>

    @Query("SELECT * FROM timetable_classes WHERE dayOfWeek = :day ORDER BY startTime ASC")
    fun getClassesForDay(day: String): Flow<List<TimetableClass>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(timetableClass: TimetableClass): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(classes: List<TimetableClass>)

    @Update
    suspend fun updateClass(timetableClass: TimetableClass)

    @Delete
    suspend fun deleteClass(timetableClass: TimetableClass)

    @Query("DELETE FROM timetable_classes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
