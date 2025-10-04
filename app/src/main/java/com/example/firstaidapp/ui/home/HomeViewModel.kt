package com.example.firstaidapp.ui.home

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.repository.GuideRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GuideRepository

    val categories: LiveData<List<String>>

    private val _navigateToGuide = MutableLiveData<String?>()
    val navigateToGuide: LiveData<String?> = _navigateToGuide

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GuideRepository(
            database.guideDao(),
            database.contactDao(),
            database.searchDao()
        )

        categories = repository.allCategories
    }

    fun callEmergencyServices(context: Context) {
        // Use ACTION_DIAL to open dialer without permission issues
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:911")
        }
        context.startActivity(intent)
    }

    fun onCategoryClicked(category: String) {
        // Handle category click
    }

    fun onNavigateToGuideComplete() {
        _navigateToGuide.value = null
    }
}
