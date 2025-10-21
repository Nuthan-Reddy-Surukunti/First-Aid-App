package com.example.firstaidapp.ui.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.models.ContactType
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.data.repository.EmergencyContactsData
import com.example.firstaidapp.utils.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val contactDao = database.contactDao()
    private val prefsManager = UserPreferencesManager(application)

    // Current selected state for filtering - load from SharedPreferences
    private val _selectedState = MutableLiveData(prefsManager.selectedState)
    val selectedState: LiveData<String> = _selectedState

    // Search query for filtering contacts
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error message
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Available states for dropdown
    private val _availableStates = MutableLiveData<List<String>>()
    val availableStates: LiveData<List<String>> = _availableStates

    // All contacts based on selected state
    val allContacts: LiveData<List<EmergencyContact>> =
        selectedState.switchMap { state ->
            if (state == "National") {
                contactDao.getAllContacts()
            } else {
                contactDao.getContactsByStateWithNational(state)
            }.asLiveData()
        }

    // Filtered contacts based on search query
    val filteredContacts: LiveData<List<EmergencyContact>> =
        searchQuery.switchMap { query ->
            if (query.isBlank()) {
                // Return empty list when query is blank, as allContacts is used for non-search
                MutableLiveData(emptyList())
            } else {
                contactDao.searchContacts(query).asLiveData()
            }
        }

    init {
        loadAvailableStates()
    }

    fun isStateSelected(): Boolean {
        return prefsManager.isStateSelectionDone
    }

    fun setSelectedState(state: String) {
        _selectedState.value = state
        // Persist to SharedPreferences
        prefsManager.selectedState = state
        prefsManager.isStateSelectionDone = true
    }
    
    fun clearSelectedState() {
        _selectedState.value = "National"
        // Persist to SharedPreferences
        prefsManager.selectedState = "National"
        prefsManager.isStateSelectionDone = false
    }

    fun searchContacts(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun addContact(contact: EmergencyContact) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                contactDao.insertContact(contact)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add contact: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateContact(contact: EmergencyContact) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                contactDao.updateContact(contact)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update contact: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (contact.isDefault) {
                    // Soft delete for default contacts
                    contactDao.softDeleteContact(contact.id)
                } else {
                    // Hard delete for user-added contacts
                    contactDao.deleteContact(contact)
                }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete contact: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getContactsByType(type: ContactType): LiveData<List<EmergencyContact>> {
        return contactDao.getContactsByType(type).asLiveData()
    }

    fun refreshContacts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                loadAvailableStates()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to refresh contacts: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadAvailableStates() {
        viewModelScope.launch {
            try {
                val states = contactDao.getAvailableStates().toMutableList()
                // Ensure "National" is always first in the list
                states.remove("National")
                states.add(0, "National")
                _availableStates.value = states
            } catch (e: Exception) {
                // Fallback to predefined states if database query fails
                val fallbackStates = mutableListOf("National")
                fallbackStates.addAll(EmergencyContactsData.getStatesList())
                _availableStates.value = fallbackStates
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Contacts Permission Management
    fun hasAskedContactsPermission(): Boolean {
        return prefsManager.hasAskedContactsPermission
    }

    fun setContactsPermissionAsked(asked: Boolean) {
        prefsManager.hasAskedContactsPermission = asked
    }

    fun isContactsPermissionGranted(): Boolean {
        return prefsManager.contactsPermissionGranted
    }

    fun setContactsPermissionGranted(granted: Boolean) {
        prefsManager.contactsPermissionGranted = granted
        prefsManager.hasAskedContactsPermission = true
    }

    override fun onCleared() {
        super.onCleared()
    }
}
