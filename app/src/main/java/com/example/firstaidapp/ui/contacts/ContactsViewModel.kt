package com.example.firstaidapp.ui.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.data.repository.GuideRepository
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: GuideRepository
    private lateinit var contactDao: com.example.firstaidapp.data.database.ContactDao

    private val _selectedState = MutableLiveData<String?>(null)
    val selectedState: LiveData<String?> = _selectedState

    val allContacts: LiveData<List<EmergencyContact>> = _selectedState.switchMap { state ->
        if (state.isNullOrEmpty()) {
            contactDao.getAllContacts()
        } else {
            contactDao.getContactsByState(state)
        }
    }

    private val _filteredContacts = MutableLiveData<List<EmergencyContact>>()
    val filteredContacts: LiveData<List<EmergencyContact>> = _filteredContacts

    init {
        viewModelScope.launch {
            val database = AppDatabase.getDatabase(application)
            contactDao = database.contactDao()
            repository = GuideRepository(
                database.guideDao(),
                contactDao,
                database.searchDao()
            )
        }
    }

    fun setSelectedState(state: String?) {
        _selectedState.value = state
    }

    fun addContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.insertContact(contact)
        }
    }

    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    fun searchContacts(query: String) {
        viewModelScope.launch {
            val contacts = allContacts.value ?: emptyList()
            val filtered = contacts.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.phoneNumber.contains(query, ignoreCase = true) ||
                it.relationship?.contains(query, ignoreCase = true) == true
            }
            _filteredContacts.postValue(filtered)
        }
    }

    fun clearSearch() {
        _filteredContacts.postValue(allContacts.value ?: emptyList())
    }
}
