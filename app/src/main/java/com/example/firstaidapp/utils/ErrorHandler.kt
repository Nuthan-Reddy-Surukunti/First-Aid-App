package com.example.firstaidapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.IOException

/**
 * Comprehensive error handling and offline support utilities
 * Ensures app functionality even without internet connection
 */
object ErrorHandler {

    private const val TAG = "FirstAidApp_ErrorHandler"

    // Global coroutine exception handler
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, "Coroutine exception occurred", exception)
        handleException(exception)
    }

    // Application scope for background operations
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main + coroutineExceptionHandler)

    /**
     * Centralized exception handling
     */
    fun handleException(exception: Throwable) {
        when (exception) {
            is IOException -> {
                Log.e(TAG, "Network/IO error: ${exception.message}")
                // Handle network errors gracefully - app works offline
            }
            is IllegalStateException -> {
                Log.e(TAG, "State error: ${exception.message}")
                // Handle state management errors
            }
            is SecurityException -> {
                Log.e(TAG, "Security error: ${exception.message}")
                // Handle permission errors
            }
            else -> {
                Log.e(TAG, "Unexpected error: ${exception.message}", exception)
            }
        }
    }

    /**
     * Checks network connectivity status
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Safe execution wrapper for database operations
     */
    fun <T> safeDatabaseOperation(
        operation: () -> T,
        onError: (Exception) -> T
    ): T {
        return try {
            operation()
        } catch (e: Exception) {
            Log.e(TAG, "Database operation failed", e)
            onError(e)
        }
    }

    /**
     * Safe execution wrapper for UI operations
     */
    fun safeUIOperation(operation: () -> Unit) {
        try {
            operation()
        } catch (e: Exception) {
            Log.e(TAG, "UI operation failed", e)
            handleException(e)
        }
    }

    /**
     * Validates app critical resources exist
     */
    fun validateCriticalResources(context: Context): Boolean {
        return try {
            // Check if critical asset files exist
            val assetManager = context.assets
            assetManager.open("guides/first_aid_data.json").close()
            true
        } catch (e: IOException) {
            Log.e(TAG, "Critical resources missing", e)
            false
        }
    }

    /**
     * Creates user-friendly error messages
     */
    fun getUserFriendlyErrorMessage(exception: Throwable): String {
        return when (exception) {
            is IOException -> "Unable to access data. Please check your storage permissions."
            is SecurityException -> "Permission required. Please grant necessary permissions."
            is IllegalStateException -> "App is in an unexpected state. Please restart the app."
            else -> "An unexpected error occurred. The app will continue to work offline."
        }
    }
}
