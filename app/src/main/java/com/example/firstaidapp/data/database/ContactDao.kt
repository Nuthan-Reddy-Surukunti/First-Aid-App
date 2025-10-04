package com.example.firstaidapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.firstaidapp.data.models.EmergencyContact

@Dao
interface ContactDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY isDefault DESC, type, name")
    fun getAllContacts(): LiveData<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE id = :contactId")
    fun getContactById(contactId: Long): LiveData<EmergencyContact?>

    @Query("SELECT * FROM emergency_contacts WHERE isDefault = 1 ORDER BY type")
    fun getDefaultContacts(): LiveData<List<EmergencyContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<EmergencyContact>)

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)

    @Query("DELETE FROM emergency_contacts WHERE isDefault = 0")
    suspend fun deleteUserContacts()
}
