package com.example.firstaidapp.utils

import android.content.Context
import android.content.SharedPreferences

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
    }

    // Theme Settings
    var isDarkModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "system") ?: "system"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    // App Settings
    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    var requireEmergencyConfirmation: Boolean
        get() = prefs.getBoolean(KEY_EMERGENCY_CONFIRMATION, true)
        set(value) = prefs.edit().putBoolean(KEY_EMERGENCY_CONFIRMATION, value).apply()

    // Guide Settings
    var fontSize: String
        get() = prefs.getString(KEY_FONT_SIZE, "medium") ?: "medium"
        set(value) = prefs.edit().putString(KEY_FONT_SIZE, value).apply()

    var showImages: Boolean
        get() = prefs.getBoolean(KEY_SHOW_IMAGES, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_IMAGES, value).apply()

    var autoScroll: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SCROLL, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_SCROLL, value).apply()

    // Search Settings
    var saveSearchHistory: Boolean
        get() = prefs.getBoolean(KEY_SAVE_SEARCH_HISTORY, true)
        set(value) = prefs.edit().putBoolean(KEY_SAVE_SEARCH_HISTORY, value).apply()

    var clearHistoryOnExit: Boolean
        get() = prefs.getBoolean(KEY_CLEAR_HISTORY_ON_EXIT, false)
        set(value) = prefs.edit().putBoolean(KEY_CLEAR_HISTORY_ON_EXIT, value).apply()

    // Privacy Settings
    var analyticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANALYTICS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_ANALYTICS_ENABLED, value).apply()

    var crashReportingEnabled: Boolean
        get() = prefs.getBoolean(KEY_CRASH_REPORTING, true)
        set(value) = prefs.edit().putBoolean(KEY_CRASH_REPORTING, value).apply()

    // Emergency Settings
    var quickDialEnabled: Boolean
        get() = prefs.getBoolean(KEY_QUICK_DIAL_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_QUICK_DIAL_ENABLED, value).apply()

    var emergencyContactsCount: Int
        get() = prefs.getInt(KEY_EMERGENCY_CONTACTS_COUNT, 5)
        set(value) = prefs.edit().putInt(KEY_EMERGENCY_CONTACTS_COUNT, value).apply()

    // Onboarding
    var isFirstTimeUser: Boolean
        get() = prefs.getBoolean(KEY_FIRST_TIME_USER, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_TIME_USER, value).apply()

    var isTutorialCompleted: Boolean
        get() = prefs.getBoolean(KEY_TUTORIAL_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_TUTORIAL_COMPLETED, value).apply()

    // Utility methods
    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }

    fun exportPreferences(): Map<String, Any?> {
        return prefs.all
    }

    fun importPreferences(preferences: Map<String, Any?>) {
        val editor = prefs.edit()
        preferences.forEach { (key, value) ->
            when (value) {
                is Boolean -> editor.putBoolean(key, value)
                is String -> editor.putString(key, value)
                is Int -> editor.putInt(key, value)
                is Float -> editor.putFloat(key, value)
                is Long -> editor.putLong(key, value)
            }
        }
        editor.apply()
    }
}
