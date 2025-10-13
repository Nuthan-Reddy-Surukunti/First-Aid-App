package com.example.firstaidapp.ui.guide

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

class GuideDetailViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: GuideRepository

    private val _guideId = MutableLiveData<String>()
    lateinit var guide: LiveData<FirstAidGuide?>

    init {
        viewModelScope.launch {
            val database = AppDatabase.getDatabase(application)
            repository = GuideRepository(
                database.guideDao(),
                database.contactDao(),
                database.searchDao()
            )
            guide = _guideId.switchMap { id ->
                repository.getGuideById(id)
            }
        }
    }

    fun loadGuide(guideId: String) {
        _guideId.value = guideId

        viewModelScope.launch {
            repository.updateLastAccessed(guideId)
        }
    }

    fun toggleFavorite(guideId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(guideId, isFavorite)
        }
    }
}
