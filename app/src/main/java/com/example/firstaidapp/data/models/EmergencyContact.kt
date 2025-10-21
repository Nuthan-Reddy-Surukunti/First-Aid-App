package com.example.firstaidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val type: ContactType,
    val state: String = "National", // State or "National" for country-wide contacts
    val isDefault: Boolean = false, // True for system-provided contacts, false for user-added
    val description: String? = null,
    val relationship: String? = null, // Relationship to the user (for personal contacts)
    val notes: String? = null, // Additional notes
    val isActive: Boolean = true
)
