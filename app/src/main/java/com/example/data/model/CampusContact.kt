package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ContactCategory {
    EMERGENCY,
    ADMINISTRATION,
    ACADEMIC,
    SUPPORT
}

@Entity(tableName = "campus_contacts")
data class CampusContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val department: String,
    val role: String,
    val phone: String,
    val email: String,
    val location: String,
    val category: ContactCategory = ContactCategory.ADMINISTRATION,
    val isEmergency: Boolean = false
)
