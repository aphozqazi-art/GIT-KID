package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CampusContact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM campus_contacts ORDER BY isEmergency DESC, name ASC")
    fun getAllContacts(): Flow<List<CampusContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: CampusContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<CampusContact>)

    @Update
    suspend fun updateContact(contact: CampusContact)

    @Delete
    suspend fun deleteContact(contact: CampusContact)

    @Query("DELETE FROM campus_contacts WHERE id = :id")
    suspend fun deleteById(id: Long)
}
