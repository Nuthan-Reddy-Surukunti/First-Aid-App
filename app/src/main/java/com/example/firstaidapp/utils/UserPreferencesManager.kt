package com.example.firstaidapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * User preferences and customization settings manager
 * Handles app theme, accessibility settings, and user customizations
 */
class UserPreferencesManager private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesManager? = null

        fun getInstance(context: Context): UserPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferencesManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        // Preference keys
        private const val PREFS_NAME = "firstaid_preferences"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_VOICE_GUIDANCE_ENABLED = "voice_guidance_enabled"
        private const val KEY_HAPTIC_FEEDBACK_ENABLED = "haptic_feedback_enabled"
        private const val KEY_AUTO_BACKUP_ENABLED = "auto_backup_enabled"
        private const val KEY_EMERGENCY_CONTACT_QUICK_DIAL = "emergency_quick_dial"
        private const val KEY_CPR_METRONOME_BPM = "cpr_metronome_bpm"
        private const val KEY_SEARCH_HISTORY_ENABLED = "search_history_enabled"
        private const val KEY_ACCESSIBILITY_MODE = "accessibility_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // LiveData for reactive preferences
    private val _themeMode = MutableLiveData<ThemeMode>()
    val themeMode: LiveData<ThemeMode> = _themeMode

    private val _fontSize = MutableLiveData<FontSize>()
    val fontSize: LiveData<FontSize> = _fontSize

    private val _accessibilityMode = MutableLiveData<Boolean>()
    val accessibilityMode: LiveData<Boolean> = _accessibilityMode

    enum class ThemeMode {
        LIGHT, DARK, SYSTEM_DEFAULT
    }

    enum class FontSize {
        SMALL, NORMAL, LARGE, EXTRA_LARGE
    }

    init {
        // Initialize LiveData with current values
        _themeMode.value = getThemeMode()
        _fontSize.value = getFontSize()
        _accessibilityMode.value = isAccessibilityModeEnabled()
    }

    // Theme preferences
    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun getThemeMode(): ThemeMode {
        val mode = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM_DEFAULT.name)
        return ThemeMode.valueOf(mode ?: ThemeMode.SYSTEM_DEFAULT.name)
    }

    // Font size preferences
    fun setFontSize(size: FontSize) {
        prefs.edit().putString(KEY_FONT_SIZE, size.name).apply()
        _fontSize.value = size
    }

    fun getFontSize(): FontSize {
        val size = prefs.getString(KEY_FONT_SIZE, FontSize.NORMAL.name)
        return FontSize.valueOf(size ?: FontSize.NORMAL.name)
    }

    // Voice guidance preferences
    fun setVoiceGuidanceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VOICE_GUIDANCE_ENABLED, enabled).apply()
    }

    fun isVoiceGuidanceEnabled(): Boolean {
        return prefs.getBoolean(KEY_VOICE_GUIDANCE_ENABLED, true)
    }

    // Haptic feedback preferences
    fun setHapticFeedbackEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC_FEEDBACK_ENABLED, enabled).apply()
    }

    fun isHapticFeedbackEnabled(): Boolean {
        return prefs.getBoolean(KEY_HAPTIC_FEEDBACK_ENABLED, true)
    }

    // Auto backup preferences
    fun setAutoBackupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_BACKUP_ENABLED, enabled).apply()
    }

    fun isAutoBackupEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_BACKUP_ENABLED, true)
    }

    // Emergency contact preferences
    fun setEmergencyQuickDialEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_EMERGENCY_CONTACT_QUICK_DIAL, enabled).apply()
    }

    fun isEmergencyQuickDialEnabled(): Boolean {
        return prefs.getBoolean(KEY_EMERGENCY_CONTACT_QUICK_DIAL, true)
    }

    // CPR metronome preferences
    fun setCPRMetronomeBPM(bpm: Int) {
        val validBPM = bpm.coerceIn(100, 120) // Ensure valid CPR range
        prefs.edit().putInt(KEY_CPR_METRONOME_BPM, validBPM).apply()
    }

    fun getCPRMetronomeBPM(): Int {
        return prefs.getInt(KEY_CPR_METRONOME_BPM, 110) // Default 110 BPM
    }

    // Search history preferences
    fun setSearchHistoryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SEARCH_HISTORY_ENABLED, enabled).apply()
    }

    fun isSearchHistoryEnabled(): Boolean {
        return prefs.getBoolean(KEY_SEARCH_HISTORY_ENABLED, true)
    }

    // Accessibility preferences
    fun setAccessibilityModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ACCESSIBILITY_MODE, enabled).apply()
        _accessibilityMode.value = enabled
    }

    fun isAccessibilityModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_ACCESSIBILITY_MODE, false)
    }

    // Language preferences
    fun setLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    // First launch tracking
    fun setFirstLaunchCompleted() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    // Export all preferences for backup
    fun exportAllPreferences(): Map<String, Any> {
        return mapOf(
            KEY_THEME_MODE to getThemeMode().name,
            KEY_FONT_SIZE to getFontSize().name,
            KEY_VOICE_GUIDANCE_ENABLED to isVoiceGuidanceEnabled(),
            KEY_HAPTIC_FEEDBACK_ENABLED to isHapticFeedbackEnabled(),
            KEY_AUTO_BACKUP_ENABLED to isAutoBackupEnabled(),
            KEY_EMERGENCY_CONTACT_QUICK_DIAL to isEmergencyQuickDialEnabled(),
            KEY_CPR_METRONOME_BPM to getCPRMetronomeBPM(),
            KEY_SEARCH_HISTORY_ENABLED to isSearchHistoryEnabled(),
            KEY_ACCESSIBILITY_MODE to isAccessibilityModeEnabled(),
            KEY_LANGUAGE to getLanguage()
        )
    }

    // Import preferences from backup
    fun importPreferences(preferences: Map<String, Any>) {
        val editor = prefs.edit()
        preferences.forEach { (key, value) ->
            when (value) {
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is String -> editor.putString(key, value)
            }
        }
        editor.apply()

        // Update LiveData
        _themeMode.value = getThemeMode()
        _fontSize.value = getFontSize()
        _accessibilityMode.value = isAccessibilityModeEnabled()
    }
}
