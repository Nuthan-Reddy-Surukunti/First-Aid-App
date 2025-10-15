package com.example.firstaidapp.ui.home

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.data.repository.GuideRepository
import com.example.firstaidapp.utils.DataInitializer
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: GuideRepository

    private val _categorizedItems = MutableLiveData<List<CategoryItem>>()
    val categorizedItems: LiveData<List<CategoryItem>> = _categorizedItems

    private val expandedCategories = mutableSetOf<String>()

    private val _guides = MutableLiveData<List<FirstAidGuide>>()

    private val _searchResults = MutableLiveData<List<FirstAidGuide>>()
    val searchResults: LiveData<List<FirstAidGuide>> = _searchResults

    init {
        viewModelScope.launch {
            val database = AppDatabase.getDatabase(application)
            repository = GuideRepository(
                database.guideDao(),
                database.contactDao(),
                database.searchDao()
            )

            Log.d("HomeViewModel", "Forcing immediate reinitialization")
            DataInitializer(getApplication()).forceImmediateReinitialization()

            repository.allGuides.observeForever { guides ->
                Log.d("HomeViewModel", "Total guides available: ${guides.size}")
                _guides.value = guides
                updateCategorizedItems(guides)
            }
        }
    }

    private fun updateCategorizedItems(guides: List<FirstAidGuide>) {
        val categorizedGuides = GuideCategories.getCategorizedGuides(guides)
        val items = mutableListOf<CategoryItem>()

        categorizedGuides.forEach { category ->
            items.add(
                CategoryItem.CategoryHeader(
                    title = category.title,
                    icon = category.icon,
                    description = category.description,
                    isExpanded = expandedCategories.contains(category.title),
                    guideCount = category.guides.size
                )
            )

            if (expandedCategories.contains(category.title)) {
                items.addAll(category.guides.map { CategoryItem.GuideItem(it) })
            }
        }

        _categorizedItems.value = items
    }

    fun toggleCategory(categoryTitle: String) {
        if (expandedCategories.contains(categoryTitle)) {
            expandedCategories.remove(categoryTitle)
        } else {
            expandedCategories.add(categoryTitle)
        }
        _guides.value?.let { updateCategorizedItems(it) }
    }

    fun searchGuides(query: String) {
        viewModelScope.launch {
            _searchResults.value = if (query.isNotEmpty()) {
                repository.searchGuidesList(query)
            } else {
                emptyList()
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    fun callEmergencyServices(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:911".toUri()
        }
        context.startActivity(intent)
    }
}
