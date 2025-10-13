package com.example.firstaidapp.data.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "emergency_contacts",
    indices = [Index(value = ["phoneNumber", "type"], unique = true)]
)
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val relationship: String? = null,
    val type: ContactType = ContactType.PERSONAL,
    val isDefault: Boolean = false,
    val isFavorite: Boolean = false,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ContactType {
    EMERGENCY_SERVICE,
    POISON_CONTROL,
    HOSPITAL,
    POLICE,
    FIRE_DEPARTMENT,
    PERSONAL,
    FAMILY,
    DOCTOR,
    VETERINARIAN,
    OTHER
}
