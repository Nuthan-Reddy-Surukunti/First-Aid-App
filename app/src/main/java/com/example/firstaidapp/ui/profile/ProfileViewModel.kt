package com.example.firstaidapp.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firstaidapp.data.MedicalInfo
import com.example.firstaidapp.data.UserProfile
import com.example.firstaidapp.utils.UserPreferencesManager

/**
 * ViewModel for Profile screen
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = UserPreferencesManager(application)

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    private val _medicalInfo = MutableLiveData<MedicalInfo>()
    val medicalInfo: LiveData<MedicalInfo> = _medicalInfo

    init {
        loadUserProfile()
        loadMedicalInfo()
    }

    /**
     * Load user profile from preferences
     */
    private fun loadUserProfile() {
        val profile = UserProfile(
            name = prefsManager.userName,
            bio = prefsManager.userBio,
            profileImageUri = prefsManager.profileImageUri,
            dateJoined = prefsManager.dateJoined
        )
        _userProfile.value = profile
    }

    /**
     * Load medical information from preferences
     */
    private fun loadMedicalInfo() {
        val medical = MedicalInfo(
            bloodType = prefsManager.bloodType,
            allergies = prefsManager.allergies,
            medications = prefsManager.medications,
            medicalConditions = prefsManager.medicalConditions,
            emergencyNotes = prefsManager.emergencyNotes,
            doctorName = prefsManager.doctorName,
            doctorContact = prefsManager.doctorContact
        )
        _medicalInfo.value = medical
    }

    /**
     * Save user profile
     */
    fun saveUserProfile(name: String, bio: String, imageUri: String = "") {
        prefsManager.userName = name
        prefsManager.userBio = bio
        if (imageUri.isNotEmpty()) {
            prefsManager.profileImageUri = imageUri
        }
        loadUserProfile()
    }

    /**
     * Save medical information
     */
    fun saveMedicalInfo(
        bloodType: String,
        allergies: String,
        medications: String,
        medicalConditions: String,
        emergencyNotes: String,
        doctorName: String,
        doctorContact: String
    ) {
        prefsManager.bloodType = bloodType
        prefsManager.allergies = allergies
        prefsManager.medications = medications
        prefsManager.medicalConditions = medicalConditions
        prefsManager.emergencyNotes = emergencyNotes
        prefsManager.doctorName = doctorName
        prefsManager.doctorContact = doctorContact
        loadMedicalInfo()
    }

    /**
     * Get user initials for profile picture placeholder
     */
    fun getUserInitials(): String {
        val name = _userProfile.value?.name ?: return "?"
        if (name.isEmpty()) return "?"

        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            else -> parts[0].take(2).uppercase()
        }
    }

    /**
     * Get theme mode
     */
    fun isDarkModeEnabled(): Boolean = prefsManager.isDarkModeEnabled

    /**
     * Toggle theme mode
     */
    fun toggleDarkMode() {
        prefsManager.isDarkModeEnabled = !prefsManager.isDarkModeEnabled
    }
}

