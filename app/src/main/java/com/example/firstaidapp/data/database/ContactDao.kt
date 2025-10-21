package com.example.firstaidapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.firstaidapp.data.models.ContactType
import com.example.firstaidapp.data.models.EmergencyContact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM emergency_contacts WHERE isActive = 1 ORDER BY isDefault DESC, type ASC, name ASC")
    fun getAllContacts(): Flow<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE state = :state AND isActive = 1 ORDER BY isDefault DESC, type ASC, name ASC")
    fun getContactsByState(state: String): Flow<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE (state = :state OR state = 'National') AND isActive = 1 ORDER BY isDefault DESC, type ASC, name ASC")
    fun getContactsByStateWithNational(state: String): Flow<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE type = :type AND isActive = 1 ORDER BY isDefault DESC, name ASC")
    fun getContactsByType(type: ContactType): Flow<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE name LIKE '%' || :searchQuery || '%' AND isActive = 1 ORDER BY isDefault DESC, name ASC")
    fun searchContacts(searchQuery: String): Flow<List<EmergencyContact>>

    @Query("SELECT COUNT(*) FROM emergency_contacts")
    suspend fun getContactsCount(): Int

    @Query("SELECT DISTINCT state FROM emergency_contacts WHERE isActive = 1 ORDER BY state ASC")
    suspend fun getAvailableStates(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<EmergencyContact>)

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)

    @Query("UPDATE emergency_contacts SET isActive = 0 WHERE id = :contactId")
    suspend fun softDeleteContact(contactId: Long)

    @Query("DELETE FROM emergency_contacts")
    suspend fun deleteAllContacts()

    @Query("DELETE FROM emergency_contacts WHERE isDefault = 0")
    suspend fun deleteUserContacts()

    @Query("SELECT * FROM emergency_contacts WHERE id = :id")
    suspend fun getContactById(id: Long): EmergencyContact?
}
