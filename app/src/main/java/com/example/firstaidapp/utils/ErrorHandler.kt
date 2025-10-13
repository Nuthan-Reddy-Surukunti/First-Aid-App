package com.example.firstaidapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineExceptionHandler

object ErrorHandler {
    const val TAG = "FirstAidApp_ErrorHandler"

    // Create a coroutine exception handler
    fun createCoroutineExceptionHandler(context: Context): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "Coroutine exception caught", exception)
            handleError(context, exception)
        }
    }

    // Main error handling function
    fun handleError(context: Context, throwable: Throwable) {
        Log.e(TAG, "Error occurred: ${throwable.message}", throwable)

        val errorMessage = when (throwable) {
            is DatabaseException -> "Database error: ${throwable.message}"
            is NetworkException -> "Network error: Please check your connection"
            is DataNotFoundException -> "Requested data not found"
            is InvalidDataException -> "Invalid data provided"
            else -> "An unexpected error occurred"
        }

        showErrorToUser(context, errorMessage)
    }

    // Show error message to user
    private fun showErrorToUser(context: Context, message: String) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show error message", e)
        }
    }

    // Show error with Snackbar (for Fragments with view)
    fun showErrorSnackbar(fragment: Fragment, message: String) {
        try {
            fragment.view?.let { view ->
                Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction("OK") { }
                    .show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show error snackbar", e)
        }
    }

    // Database operation wrapper
    inline fun <T> safeDbOperation(
        context: Context,
        operation: () -> T,
        onError: (Throwable) -> T
    ): T {
        return try {
            operation()
        } catch (e: Exception) {
            Log.e(TAG, "Database operation failed", e)
            handleError(context, DatabaseException("Database operation failed", e))
            onError(e)
        }
    }

    // Network operation wrapper
    inline fun <T> safeNetworkOperation(
        context: Context,
        operation: () -> T,
        onError: (Throwable) -> T
    ): T {
        return try {
            operation()
        } catch (e: Exception) {
            Log.e(TAG, "Network operation failed", e)
            handleError(context, NetworkException("Network operation failed", e))
            onError(e)
        }
    }
}

// Custom exception classes
class DatabaseException(message: String, cause: Throwable? = null) : Exception(message, cause)
class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
class DataNotFoundException(message: String = "Data not found") : Exception(message)
class InvalidDataException(message: String = "Invalid data") : Exception(message)
