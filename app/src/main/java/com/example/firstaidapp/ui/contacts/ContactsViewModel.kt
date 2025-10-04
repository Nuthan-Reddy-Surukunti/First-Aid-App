package com.example.firstaidapp.ui.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.data.repository.GuideRepository
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GuideRepository

    val allContacts: LiveData<List<EmergencyContact>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GuideRepository(
            database.guideDao(),
            database.contactDao(),
            database.searchDao()
        )

        allContacts = repository.allContacts
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
}
