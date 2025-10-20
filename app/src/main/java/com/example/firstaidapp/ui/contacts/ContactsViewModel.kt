package com.example.firstaidapp.ui.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.data.repository.GuideRepository
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: GuideRepository

    lateinit var allContacts: LiveData<List<EmergencyContact>>

    private val _filteredContacts = MutableLiveData<List<EmergencyContact>>()
    val filteredContacts: LiveData<List<EmergencyContact>> = _filteredContacts

    init {
        viewModelScope.launch {
            val database = AppDatabase.getDatabase(application)
            repository = GuideRepository(
                database.guideDao(),
                database.contactDao(),
                database.searchDao()
            )

            allContacts = repository.allContacts
        }
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
