package com.example.firstaidapp.utils

object Constants {
    // Emergency Numbers
    const val EMERGENCY_NUMBER_US = "911"
    const val POISON_CONTROL_US = "1-800-222-1222"

    // Categories
    const val CATEGORY_CPR = "CPR"
    const val CATEGORY_BLEEDING = "Bleeding"
    const val CATEGORY_BURNS = "Burns"
    const val CATEGORY_CHOKING = "Choking"
    const val CATEGORY_FRACTURES = "Fractures"
    const val CATEGORY_POISONING = "Poisoning"
    const val CATEGORY_SHOCK = "Shock"
    const val CATEGORY_BREATHING = "Breathing Problems"

    // Severity Levels
    const val SEVERITY_LOW = "LOW"
    const val SEVERITY_MEDIUM = "MEDIUM"
    const val SEVERITY_HIGH = "HIGH"
    const val SEVERITY_CRITICAL = "CRITICAL"

    // Shared Preferences
    const val PREFS_NAME = "FirstAidPrefs"
    const val KEY_FIRST_LAUNCH = "isFirstLaunch"
    const val KEY_DATA_INITIALIZED = "isDataInitialized"

    // Database
    const val DATABASE_NAME = "first_aid_database"
    const val DATABASE_VERSION = 1

    // CPR Metronome
    const val CPR_BPM_MIN = 100
    const val CPR_BPM_MAX = 120
    const val CPR_BPM_DEFAULT = 110
    const val CPR_COMPRESSIONS_PER_CYCLE = 30
    const val CPR_BREATHS_PER_CYCLE = 2

    // Search
    const val MAX_SEARCH_HISTORY = 5

    // Navigation
    const val NAV_ARG_GUIDE_ID = "guide_id"
    const val NAV_ARG_CATEGORY = "category"
}
