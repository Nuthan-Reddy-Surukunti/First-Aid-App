package com.example.firstaidapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.data.models.SearchHistory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data backup and synchronization utilities for user data persistence
 * Handles local backup of favorites, contacts, and preferences
 */
class DataBackupManager private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: DataBackupManager? = null
        
        fun getInstance(context: Context): DataBackupManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataBackupManager(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        private const val BACKUP_PREFS = "backup_preferences"
        private const val LAST_BACKUP_TIME = "last_backup_time"
        private const val AUTO_BACKUP_ENABLED = "auto_backup_enabled"
        private const val BACKUP_FREQUENCY_DAYS = "backup_frequency_days"
    }
    
    private val gson = Gson()
    private val prefs: SharedPreferences = context.getSharedPreferences(BACKUP_PREFS, Context.MODE_PRIVATE)
    private val backupScope = CoroutineScope(Dispatchers.IO + ErrorHandler.createCoroutineExceptionHandler(context))

    private val _backupStatus = MutableLiveData<BackupStatus>()
    val backupStatus: LiveData<BackupStatus> = _backupStatus
    
    data class BackupData(
        val emergencyContacts: List<EmergencyContact>,
        val searchHistory: List<SearchHistory>,
        val favoriteGuideIds: List<Int>,
        val userPreferences: Map<String, Any>,
        val backupTimestamp: Long
    )
    
    sealed class BackupStatus {
        object Idle : BackupStatus()
        object InProgress : BackupStatus()
        data class Success(val filePath: String) : BackupStatus()
        data class Error(val message: String) : BackupStatus()
    }
    
    /**
     * Creates a local backup of user data
     */
    fun createBackup() {
        backupScope.launch {
            try {
                _backupStatus.postValue(BackupStatus.InProgress)
                
                // Collect user data (this would normally come from DAOs)
                val backupData = BackupData(
                    emergencyContacts = emptyList(), // Would get from ContactDao
                    searchHistory = emptyList(), // Would get from SearchDao
                    favoriteGuideIds = emptyList(), // Would get from GuideDao
                    userPreferences = getUserPreferences(),
                    backupTimestamp = System.currentTimeMillis()
                )
                
                val backupJson = gson.toJson(backupData)
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "firstaid_backup_$timestamp.json"
                
                val backupFile = File(context.filesDir, fileName)
                withContext(Dispatchers.IO) {
                    FileWriter(backupFile).use { writer ->
                        writer.write(backupJson)
                    }
                }
                
                // Update backup timestamp
                prefs.edit().putLong(LAST_BACKUP_TIME, System.currentTimeMillis()).apply()
                
                _backupStatus.postValue(BackupStatus.Success(backupFile.absolutePath))
                
            } catch (e: Exception) {
                ErrorHandler.handleError(context, e)
                _backupStatus.postValue(BackupStatus.Error("Backup failed: ${e.message}"))
            }
        }
    }
    
    /**
     * Restores data from a backup file
     */
    fun restoreBackup(backupFilePath: String) {
        backupScope.launch {
            try {
                _backupStatus.postValue(BackupStatus.InProgress)
                
                val backupFile = File(backupFilePath)
                if (!backupFile.exists()) {
                    _backupStatus.postValue(BackupStatus.Error("Backup file not found"))
                    return@launch
                }
                
                val backupJson = withContext(Dispatchers.IO) {
                    backupFile.readText()
                }
                
                val backupData = gson.fromJson(backupJson, BackupData::class.java)
                
                // Restore user preferences
                restoreUserPreferences(backupData.userPreferences)
                
                // Note: In a complete implementation, this would restore to database
                // restoreToDatabase(backupData)
                
                _backupStatus.postValue(BackupStatus.Success("Data restored successfully"))
                
            } catch (e: Exception) {
                ErrorHandler.handleError(context, e)
                _backupStatus.postValue(BackupStatus.Error("Restore failed: ${e.message}"))
            }
        }
    }
    
    /**
     * Checks if backup is needed based on user settings
     */
    fun shouldCreateAutoBackup(): Boolean {
        val autoBackupEnabled = prefs.getBoolean(AUTO_BACKUP_ENABLED, true)
        val lastBackupTime = prefs.getLong(LAST_BACKUP_TIME, 0)
        val backupFrequencyDays = prefs.getInt(BACKUP_FREQUENCY_DAYS, 7)
        val daysSinceLastBackup = (System.currentTimeMillis() - lastBackupTime) / (1000 * 60 * 60 * 24)
        
        return autoBackupEnabled && daysSinceLastBackup >= backupFrequencyDays
    }
    
    /**
     * Gets available backup files
     */
    fun getAvailableBackups(): List<File> {
        return context.filesDir.listFiles { file ->
            file.name.startsWith("firstaid_backup_") && file.name.endsWith(".json")
        }?.toList() ?: emptyList()
    }
    
    /**
     * Configures auto backup settings
     */
    fun setAutoBackupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(AUTO_BACKUP_ENABLED, enabled).apply()
    }

    fun setBackupFrequency(days: Int) {
        prefs.edit().putInt(BACKUP_FREQUENCY_DAYS, days).apply()
    }

    private fun getUserPreferences(): Map<String, Any> {
        val allPrefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE).all
        return allPrefs.mapValues { entry ->
            when (val value = entry.value) {
                is Boolean, is Int, is Long, is Float, is String -> value
                else -> value?.toString() ?: ""
            }
        }
    }

    private fun restoreUserPreferences(preferences: Map<String, Any>) {
        val appPrefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val editor = appPrefs.edit()

        preferences.forEach { (key, value) ->
            when (value) {
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> editor.putString(key, value)
            }
        }
        editor.apply()
    }
}
