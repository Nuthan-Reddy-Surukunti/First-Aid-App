package com.example.firstaidapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    suspend fun getCurrentState(): String? {
        if (!hasLocationPermission()) {
            return null
        }
        
        val location = getCurrentLocation() ?: return null
        return getStateFromLocation(location)
    }
    
    private suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        
        try {
            val cancellationTokenSource = CancellationTokenSource()
            
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener {
                continuation.resume(null)
            }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }
    
    private fun getStateFromLocation(location: Location): String? {
        return try {
            val geocoder = Geocoder(context)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13 and above, use the new async API
                var state: String? = null
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        state = normalizeStateName(addresses[0].adminArea)
                    }
                }
                // Note: This is a limitation - we'd need to make this truly async
                // For now, fallback to the synchronous method
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    normalizeStateName(addresses[0].adminArea)
                } else {
                    null
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    normalizeStateName(addresses[0].adminArea)
                } else {
                    null
                }
            }
        } catch (e: IOException) {
            null
        } catch (e: Exception) {
            null
        }
    }
    
    private fun normalizeStateName(stateName: String?): String? {
        if (stateName == null) return null
        
        // Map common state name variations to standardized names
        return when {
            stateName.contains("Andhra Pradesh", ignoreCase = true) -> "Andhra Pradesh"
            stateName.contains("Karnataka", ignoreCase = true) -> "Karnataka"
            stateName.contains("Kerala", ignoreCase = true) -> "Kerala"
            stateName.contains("Telangana", ignoreCase = true) -> "Telangana"
            stateName.contains("Tripura", ignoreCase = true) -> "Tripura"
            stateName.contains("Delhi", ignoreCase = true) || 
                stateName.contains("NCT", ignoreCase = true) -> "Delhi"
            stateName.contains("Chandigarh", ignoreCase = true) -> "Chandigarh"
            stateName.contains("Ladakh", ignoreCase = true) -> "Ladakh"
            stateName.contains("Lakshadweep", ignoreCase = true) -> "Lakshadweep"
            stateName.contains("Andaman", ignoreCase = true) || 
                stateName.contains("Nicobar", ignoreCase = true) -> "Andaman and Nicobar Islands"
            else -> stateName
        }
    }
}
