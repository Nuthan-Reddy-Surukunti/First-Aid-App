package com.example.firstaidapp.ui.home

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.map
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.data.repository.GuideRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: GuideRepository

    lateinit var categories: LiveData<List<String>>
    lateinit var allGuides: LiveData<List<FirstAidGuide>>

    // Show all guides on home page instead of limiting to 4
    lateinit var featuredGuides: LiveData<List<FirstAidGuide>>

    private val _searchResults = MutableLiveData<List<FirstAidGuide>>()
    val searchResults: LiveData<List<FirstAidGuide>> = _searchResults

    private val _navigateToGuide = MutableLiveData<String?>()
    val navigateToGuide: LiveData<String?> = _navigateToGuide

    init {
        viewModelScope.launch {
            val database = AppDatabase.getDatabase(application)
            repository = GuideRepository(
                database.guideDao(),
                database.contactDao(),
                database.searchDao()
            )

            categories = repository.allCategories
            allGuides = repository.allGuides

            // Show all guides on home page (removed the .take(4) limitation)
            featuredGuides = allGuides.map { guides ->
                guides // Show all guides instead of limiting to 4
            }
        }

        _searchResults.value = emptyList()
    }

    fun searchGuides(query: String) {
        viewModelScope.launch {
            if (query.isNotEmpty()) {
                _searchResults.value = repository.searchGuidesList(query)
            } else {
                _searchResults.value = emptyList()
            }
        }
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
