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
    
    @Query("SELECT * FROM emergency_contacts WHERE state = :state OR state = 'National' ORDER BY CASE WHEN state = 'National' THEN 0 ELSE 1 END, type, name")
    fun getContactsByState(state: String): LiveData<List<EmergencyContact>>
    
    @Query("SELECT * FROM emergency_contacts WHERE state = 'National' ORDER BY type, name")
    fun getNationalContacts(): LiveData<List<EmergencyContact>>

    @Query("SELECT COUNT(*) FROM emergency_contacts")
    suspend fun getContactsCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertContact(contact: EmergencyContact)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(contacts: List<EmergencyContact>)

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)

    @Query("DELETE FROM emergency_contacts WHERE isDefault = 0")
    suspend fun deleteUserContacts()

    @Query("DELETE FROM emergency_contacts")
    suspend fun deleteAllContacts()
}
