package com.example.firstaidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val type: ContactType,
    val isDefault: Boolean = false, // For preset emergency numbers
    val countryCode: String = "+1"
)

enum class ContactType {
    EMERGENCY_SERVICE,  // 911, etc.
    HOSPITAL,
    POISON_CONTROL,
    PERSONAL,
    POLICE,
    FIRE_DEPARTMENT,
    AMBULANCE
}
