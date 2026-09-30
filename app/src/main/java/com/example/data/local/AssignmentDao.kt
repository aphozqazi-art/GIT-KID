package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Assignment
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments ORDER BY deadlineTimestamp ASC")
    fun getAllAssignments(): Flow<List<Assignment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assignments: List<Assignment>)

    @Update
    suspend fun updateAssignment(assignment: Assignment)

    @Delete
    suspend fun deleteAssignment(assignment: Assignment)

    @Query("DELETE FROM assignments WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE assignments SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleCompleted(id: Long, completed: Boolean)
}
