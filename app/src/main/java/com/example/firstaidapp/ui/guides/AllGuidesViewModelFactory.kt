package com.example.firstaidapp.ui.guides

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AllGuidesViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllGuidesViewModel::class.java)) {
            return AllGuidesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

