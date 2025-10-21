package com.example.firstaidapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "FirstAidUserPrefs"

        // Theme preferences
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DARK_MODE = "dark_mode"

        // App preferences
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_EMERGENCY_CONFIRMATION = "emergency_confirmation"

        // Guide preferences
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_SHOW_IMAGES = "show_images"
        private const val KEY_AUTO_SCROLL = "auto_scroll"

        // Search preferences
        private const val KEY_SAVE_SEARCH_HISTORY = "save_search_history"
        private const val KEY_CLEAR_HISTORY_ON_EXIT = "clear_history_on_exit"

        // Privacy preferences
        private const val KEY_ANALYTICS_ENABLED = "analytics_enabled"
        private const val KEY_CRASH_REPORTING = "crash_reporting"

        // Emergency preferences
        private const val KEY_QUICK_DIAL_ENABLED = "quick_dial_enabled"
        private const val KEY_EMERGENCY_CONTACTS_COUNT = "emergency_contacts_count"

        // Onboarding
        private const val KEY_FIRST_TIME_USER = "first_time_user"
        private const val KEY_TUTORIAL_COMPLETED = "tutorial_completed"

        // State selection for contacts
        private const val KEY_SELECTED_STATE = "selected_state"
        private const val KEY_STATE_SELECTION_DONE = "state_selection_done"

        // Contacts permission tracking
        private const val KEY_CONTACTS_PERMISSION_ASKED = "contacts_permission_asked"
        private const val KEY_CONTACTS_PERMISSION_GRANTED = "contacts_permission_granted"

        // Learning reminder preferences
        private const val KEY_LEARNING_REMINDERS_ENABLED = "learning_reminders_enabled"
        private const val KEY_LAST_REMINDER_DATE = "last_reminder_date"
        private const val KEY_NOTIFICATION_FREQUENCY = "notification_frequency"
        private const val KEY_NOTIFICATION_TIME_HOUR = "notification_time_hour"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_DONT_SHOW_DAILY_REMINDER = "dont_show_daily_reminder"

        // Learning progress tracking
        private const val KEY_GUIDES_OPENED_SET = "guides_opened_set"
        private const val KEY_TOTAL_GUIDES_COUNT = "total_guides_count"

        // Profile preferences
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_BIO = "user_bio"
        private const val KEY_PROFILE_IMAGE_URI = "profile_image_uri"
        private const val KEY_DATE_JOINED = "date_joined"

        // Medical information preferences
        private const val KEY_BLOOD_TYPE = "blood_type"
        private const val KEY_ALLERGIES = "allergies"
        private const val KEY_MEDICATIONS = "medications"
        private const val KEY_MEDICAL_CONDITIONS = "medical_conditions"
        private const val KEY_EMERGENCY_NOTES = "emergency_notes"
        private const val KEY_DOCTOR_NAME = "doctor_name"
        private const val KEY_DOCTOR_CONTACT = "doctor_contact"
    }

    // Theme Settings
    var isDarkModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit { putBoolean(KEY_DARK_MODE, value) }

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "system") ?: "system"
        set(value) = prefs.edit { putString(KEY_THEME_MODE, value) }

    // App Settings
    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_SOUND_ENABLED, value) }

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_VIBRATION_ENABLED, value) }

    var requireEmergencyConfirmation: Boolean
        get() = prefs.getBoolean(KEY_EMERGENCY_CONFIRMATION, true)
        set(value) = prefs.edit { putBoolean(KEY_EMERGENCY_CONFIRMATION, value) }

    // Guide Settings
    var fontSize: String
        get() = prefs.getString(KEY_FONT_SIZE, "medium") ?: "medium"
        set(value) = prefs.edit { putString(KEY_FONT_SIZE, value) }

    var showImages: Boolean
        get() = prefs.getBoolean(KEY_SHOW_IMAGES, true)
        set(value) = prefs.edit { putBoolean(KEY_SHOW_IMAGES, value) }

    var autoScroll: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SCROLL, false)
        set(value) = prefs.edit { putBoolean(KEY_AUTO_SCROLL, value) }

    // Search Settings
    var saveSearchHistory: Boolean
        get() = prefs.getBoolean(KEY_SAVE_SEARCH_HISTORY, true)
        set(value) = prefs.edit { putBoolean(KEY_SAVE_SEARCH_HISTORY, value) }

    var clearHistoryOnExit: Boolean
        get() = prefs.getBoolean(KEY_CLEAR_HISTORY_ON_EXIT, false)
        set(value) = prefs.edit { putBoolean(KEY_CLEAR_HISTORY_ON_EXIT, value) }

    // Privacy Settings
    var analyticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANALYTICS_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_ANALYTICS_ENABLED, value) }

    var crashReportingEnabled: Boolean
        get() = prefs.getBoolean(KEY_CRASH_REPORTING, true)
        set(value) = prefs.edit { putBoolean(KEY_CRASH_REPORTING, value) }

    // Emergency Settings
    var quickDialEnabled: Boolean
        get() = prefs.getBoolean(KEY_QUICK_DIAL_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_QUICK_DIAL_ENABLED, value) }

    var emergencyContactsCount: Int
        get() = prefs.getInt(KEY_EMERGENCY_CONTACTS_COUNT, 5)
        set(value) = prefs.edit { putInt(KEY_EMERGENCY_CONTACTS_COUNT, value) }

    // Onboarding
    var isFirstTimeUser: Boolean
        get() = prefs.getBoolean(KEY_FIRST_TIME_USER, true)
        set(value) = prefs.edit { putBoolean(KEY_FIRST_TIME_USER, value) }

    var isTutorialCompleted: Boolean
        get() = prefs.getBoolean(KEY_TUTORIAL_COMPLETED, false)
        set(value) = prefs.edit { putBoolean(KEY_TUTORIAL_COMPLETED, value) }

    // State Selection for Contacts
    var selectedState: String
        get() = prefs.getString(KEY_SELECTED_STATE, "National") ?: "National"
        set(value) = prefs.edit { putString(KEY_SELECTED_STATE, value) }

    var isStateSelectionDone: Boolean
        get() = prefs.getBoolean(KEY_STATE_SELECTION_DONE, false)
        set(value) = prefs.edit { putBoolean(KEY_STATE_SELECTION_DONE, value) }

    // Contacts Permission Tracking
    var hasAskedContactsPermission: Boolean
        get() = prefs.getBoolean(KEY_CONTACTS_PERMISSION_ASKED, false)
        set(value) = prefs.edit { putBoolean(KEY_CONTACTS_PERMISSION_ASKED, value) }

    var contactsPermissionGranted: Boolean
        get() = prefs.getBoolean(KEY_CONTACTS_PERMISSION_GRANTED, false)
        set(value) = prefs.edit { putBoolean(KEY_CONTACTS_PERMISSION_GRANTED, value) }

    // Learning Reminder Settings
    var learningRemindersEnabled: Boolean
        get() = prefs.getBoolean(KEY_LEARNING_REMINDERS_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_LEARNING_REMINDERS_ENABLED, value) }

    var lastReminderDate: String
        get() = prefs.getString(KEY_LAST_REMINDER_DATE, "") ?: ""
        set(value) = prefs.edit { putString(KEY_LAST_REMINDER_DATE, value) }

    var notificationFrequency: String
        get() = prefs.getString(KEY_NOTIFICATION_FREQUENCY, "daily") ?: "daily"
        set(value) = prefs.edit { putString(KEY_NOTIFICATION_FREQUENCY, value) }

    var notificationTimeHour: Int
        get() = prefs.getInt(KEY_NOTIFICATION_TIME_HOUR, 9) // Default 9 AM
        set(value) = prefs.edit { putInt(KEY_NOTIFICATION_TIME_HOUR, value) }

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit { putBoolean(KEY_ONBOARDING_COMPLETED, value) }

    var dontShowDailyReminder: Boolean
        get() = prefs.getBoolean(KEY_DONT_SHOW_DAILY_REMINDER, false)
        set(value) = prefs.edit { putBoolean(KEY_DONT_SHOW_DAILY_REMINDER, value) }

    // Learning Progress Tracking
    fun markGuideAsOpened(guideId: Int) {
        val openedGuides = getOpenedGuides().toMutableSet()
        openedGuides.add(guideId)
        prefs.edit { putStringSet(KEY_GUIDES_OPENED_SET, openedGuides.map { it.toString() }.toSet()) }
    }

    fun getOpenedGuides(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_GUIDES_OPENED_SET, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun getOpenedGuidesCount(): Int {
        return getOpenedGuides().size
    }

    var totalGuidesCount: Int
        get() = prefs.getInt(KEY_TOTAL_GUIDES_COUNT, 10)
        set(value) = prefs.edit { putInt(KEY_TOTAL_GUIDES_COUNT, value) }

    fun getLearningProgress(): Int {
        val total = totalGuidesCount
        if (total == 0) return 0
        return (getOpenedGuidesCount() * 100) / total
    }

    // Profile Settings
    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit { putString(KEY_USER_NAME, value) }

    var userBio: String
        get() = prefs.getString(KEY_USER_BIO, "") ?: ""
        set(value) = prefs.edit { putString(KEY_USER_BIO, value) }

    var profileImageUri: String
        get() = prefs.getString(KEY_PROFILE_IMAGE_URI, "") ?: ""
        set(value) = prefs.edit { putString(KEY_PROFILE_IMAGE_URI, value) }

    var dateJoined: Long
        get() = prefs.getLong(KEY_DATE_JOINED, System.currentTimeMillis())
        set(value) = prefs.edit { putLong(KEY_DATE_JOINED, value) }

    // Medical Information Settings
    var bloodType: String
        get() = prefs.getString(KEY_BLOOD_TYPE, "") ?: ""
        set(value) = prefs.edit { putString(KEY_BLOOD_TYPE, value) }

    var allergies: String
        get() = prefs.getString(KEY_ALLERGIES, "") ?: ""
        set(value) = prefs.edit { putString(KEY_ALLERGIES, value) }

    var medications: String
        get() = prefs.getString(KEY_MEDICATIONS, "") ?: ""
        set(value) = prefs.edit { putString(KEY_MEDICATIONS, value) }

    var medicalConditions: String
        get() = prefs.getString(KEY_MEDICAL_CONDITIONS, "") ?: ""
        set(value) = prefs.edit { putString(KEY_MEDICAL_CONDITIONS, value) }

    var emergencyNotes: String
        get() = prefs.getString(KEY_EMERGENCY_NOTES, "") ?: ""
        set(value) = prefs.edit { putString(KEY_EMERGENCY_NOTES, value) }

    var doctorName: String
        get() = prefs.getString(KEY_DOCTOR_NAME, "") ?: ""
        set(value) = prefs.edit { putString(KEY_DOCTOR_NAME, value) }

    var doctorContact: String
        get() = prefs.getString(KEY_DOCTOR_CONTACT, "") ?: ""
        set(value) = prefs.edit { putString(KEY_DOCTOR_CONTACT, value) }

    // Utility methods
    fun resetToDefaults() {
        prefs.edit { clear() }
    }

    fun exportPreferences(): Map<String, Any?> {
        return prefs.all
    }

    fun importPreferences(preferences: Map<String, Any?>) {
        prefs.edit {
            preferences.forEach { (key, value) ->
                when (value) {
                    is Boolean -> putBoolean(key, value)
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Float -> putFloat(key, value)
                    is Long -> putLong(key, value)
                }
            }
        }
    }
}

