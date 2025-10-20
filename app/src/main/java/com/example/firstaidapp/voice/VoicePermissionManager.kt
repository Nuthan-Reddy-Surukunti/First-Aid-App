package com.example.firstaidapp.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * Manages permissions required for voice assistant functionality
 */
class VoicePermissionManager(private val context: Context) {

    companion object {
        const val REQUEST_CODE_RECORD_AUDIO = 1001
        const val REQUEST_CODE_LOCATION = 1002
        const val REQUEST_CODE_CALL_PHONE = 1003
        const val REQUEST_CODE_ALL_PERMISSIONS = 1000

        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
    }

    /**
     * Check if all required permissions are granted
     */
    fun areAllPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ActivityCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if audio recording permission is granted
     */
    fun isAudioPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if location permissions are granted
     */
    fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if call phone permission is granted
     */
    fun isCallPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get list of missing permissions
     */
    fun getMissingPermissions(): List<String> {
        return REQUIRED_PERMISSIONS.filter { permission ->
            ActivityCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Get permission rationale message for user
     */
    fun getPermissionRationale(permission: String): String {
        return when (permission) {
            Manifest.permission.RECORD_AUDIO ->
                "Microphone access is required for voice commands and emergency guidance"
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION ->
                "Location access is needed to find nearby hospitals during emergencies"
            Manifest.permission.CALL_PHONE ->
                "Phone permission allows quick emergency service calls"
            Manifest.permission.MODIFY_AUDIO_SETTINGS ->
                "Audio settings access ensures you can hear emergency instructions clearly"
            else -> "This permission is required for the voice assistant to function properly"
        }
    }
}

