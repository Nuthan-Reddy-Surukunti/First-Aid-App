package com.example.firstaidapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.firstaidapp.utils.LearningDialogManager
import com.example.firstaidapp.utils.LearningNotificationManager
import com.example.firstaidapp.voice.VoicePermissionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * MainActivity - Main entry point of the First Aid Emergency Guide App
 */
class MainActivity : AppCompatActivity() {

    private lateinit var voicePermissionManager: VoicePermissionManager
    private lateinit var learningDialogManager: LearningDialogManager
    private lateinit var learningNotificationManager: LearningNotificationManager

    // Permission launcher for requesting multiple permissions
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handlePermissionResults(permissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Initialize permission manager
            voicePermissionManager = VoicePermissionManager(this)

            // Enable edge-to-edge display for modern Android UI
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)

            // Handle system window insets for proper edge-to-edge display
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            setupNavigation()

            // Initialize learning managers
            learningDialogManager = LearningDialogManager(this)
            learningNotificationManager = LearningNotificationManager(this)

            // Show learning dialogs
            showLearningDialogs()

            // Request required permissions for AI voice assistant
            requestRequiredPermissions()

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: set a simple content view if navigation setup fails
            try {
                setContentView(android.R.layout.activity_list_item)
            } catch (fallbackException: Exception) {
                fallbackException.printStackTrace()
            }
        }
    }

    private fun setupNavigation() {
        try {
            // Set up Navigation Component with NavHostFragment
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            val navController = navHostFragment?.navController

            // Configure bottom navigation with navigation controller
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

            if (navController != null) {
                bottomNavigationView?.setupWithNavController(navController)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Navigation setup failed, but app can still run
        }
    }

    /**
     * Show learning dialogs - welcome for first-time users, daily reminder for returning users
     */
    private fun showLearningDialogs() {
        // Delay slightly to ensure UI is fully initialized
        window.decorView.post {
            try {
                // Show welcome dialog first for new users
                learningDialogManager.showWelcomeDialog {
                    // After welcome dialog, show daily reminder if applicable
                    learningDialogManager.showDailyReminderDialog {
                        // When user clicks "Start Learning", navigate to home screen
                        navigateToHomeScreen()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Failed to show dialog, but app can continue
            }
        }
    }

    /**
     * Navigate to the home screen (guide list)
     */
    private fun navigateToHomeScreen() {
        try {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            val navController = navHostFragment?.navController

            // Navigate to home destination (guide list)
            navController?.navigate(R.id.navigation_home)

            // Also select home in bottom navigation
            findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.navigation_home
        } catch (e: Exception) {
            e.printStackTrace()
            // Navigation failed, but app can still work
        }
    }

    /**
     * Request all required permissions for AI voice assistant functionality
     */
    private fun requestRequiredPermissions() {
        if (!voicePermissionManager.areAllPermissionsGranted()) {
            val missingPermissions = voicePermissionManager.getMissingPermissions()

            // Show rationale dialog for critical permissions
            if (missingPermissions.contains(android.Manifest.permission.RECORD_AUDIO)) {
                showPermissionRationaleDialog {
                    permissionLauncher.launch(missingPermissions.toTypedArray())
                }
            } else {
                // Request permissions directly
                permissionLauncher.launch(missingPermissions.toTypedArray())
            }
        }
    }

    /**
     * Handle the results of permission requests
     */
    private fun handlePermissionResults(permissions: Map<String, Boolean>) {
        val deniedPermissions = permissions.filter { !it.value }.keys

        if (deniedPermissions.isEmpty()) {
            // All permissions granted - AI voice assistant is ready
            return
        }

        // Check for critically denied permissions
        val criticalPermissionsDenied = deniedPermissions.intersect(
            setOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CALL_PHONE
            )
        )

        if (criticalPermissionsDenied.isNotEmpty()) {
            showPermissionDeniedDialog(criticalPermissionsDenied.toList())
        }
    }

    /**
     * Show rationale dialog explaining why permissions are needed
     */
    private fun showPermissionRationaleDialog(onPositive: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.permission_microphone_rationale) + "\n\n" +
                       getString(R.string.permission_location_rationale) + "\n\n" +
                       getString(R.string.permission_call_rationale))
            .setPositiveButton(getString(R.string.allow)) { _, _ ->
                onPositive()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show dialog when critical permissions are denied
     */
    private fun showPermissionDeniedDialog(deniedPermissions: List<String>) {
        val permissionNames = deniedPermissions.map { permission ->
            when (permission) {
                android.Manifest.permission.RECORD_AUDIO -> "Microphone"
                android.Manifest.permission.ACCESS_FINE_LOCATION -> "Location"
                android.Manifest.permission.CALL_PHONE -> "Phone"
                else -> "Required Permission"
            }
        }.joinToString(", ")

        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("The following permissions are required for full AI voice assistant functionality: $permissionNames\n\nYou can enable them in Settings.")
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                // Open app settings (you could implement this to redirect to app settings)
            }
            .setNegativeButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}