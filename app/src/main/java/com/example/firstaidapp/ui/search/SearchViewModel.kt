package com.example.firstaidapp.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.data.repository.GuideRepository
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: GuideRepository

    private val _searchQuery = MutableLiveData<String>()
    lateinit var searchResults: LiveData<List<FirstAidGuide>>

    init {
        viewModelScope.launch {
            val database = AppDatabase.getDatabase(application)
            repository = GuideRepository(
                database.guideDao(),
                database.contactDao(),
                database.searchDao()
            )
            searchResults = _searchQuery.switchMap { query ->
                if (query.isNullOrBlank()) {
                    MutableLiveData(emptyList())
                } else {
                    repository.searchGuides(query)
                }
            }
        }
    }

    fun searchGuides(query: String) {
        _searchQuery.value = query

        if (query.isNotBlank()) {
            viewModelScope.launch {
                repository.saveSearch(query, 0)
            }
        }
    }
}
