package com.example.firstaidapp.utils

import android.app.AlertDialog
import android.content.Context

/**
 * Manages in-app learning reminder dialogs
 * Shows onboarding and daily reminder dialogs to encourage learning
 */
class LearningDialogManager(private val context: Context) {

    private val prefsManager = UserPreferencesManager(context)

    /**
     * Show welcome dialog for first-time users
     */
    fun showWelcomeDialog(onComplete: () -> Unit) {
        if (prefsManager.onboardingCompleted) {
            onComplete()
            return
        }

        AlertDialog.Builder(context)
            .setTitle("Welcome to First Aid Emergency Guide! 🚑")
            .setMessage(
                "Emergency preparedness saves lives!\n\n" +
                        "📚 This app contains 10 comprehensive first aid guides\n\n" +
                        "💡 We recommend:\n" +
                        "• Reading through all guides\n" +
                        "• Watching educational videos\n" +
                        "• Practicing techniques when possible\n\n" +
                        "🔔 Would you like daily reminders to continue learning?"
            )
            .setPositiveButton("Enable Reminders") { dialog, _ ->
                prefsManager.learningRemindersEnabled = true
                prefsManager.onboardingCompleted = true

                // Schedule notifications
                val notificationManager = LearningNotificationManager(context)
                notificationManager.scheduleDailyNotifications()

                dialog.dismiss()
                showNotificationFrequencyDialog(onComplete)
            }
            .setNegativeButton("Maybe Later") { dialog, _ ->
                prefsManager.learningRemindersEnabled = false
                prefsManager.onboardingCompleted = true
                dialog.dismiss()
                onComplete()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Show dialog to set notification frequency
     */
    private fun showNotificationFrequencyDialog(onComplete: () -> Unit) {
        val frequencies = arrayOf("Daily", "Every 2 Days", "Weekly")
        val frequencyValues = arrayOf("daily", "every_2_days", "weekly")
        var selectedIndex = 0

        AlertDialog.Builder(context)
            .setTitle("Notification Frequency")
            .setSingleChoiceItems(frequencies, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Set") { dialog, _ ->
                prefsManager.notificationFrequency = frequencyValues[selectedIndex]

                // Reschedule notifications with new frequency
                val notificationManager = LearningNotificationManager(context)
                notificationManager.scheduleDailyNotifications()

                dialog.dismiss()
                onComplete()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Show daily learning reminder dialog
     * @param onStartLearning Callback when user clicks "Start Learning" button
     */
    fun showDailyReminderDialog(onStartLearning: () -> Unit = {}) {
        val notificationManager = LearningNotificationManager(context)

        if (!notificationManager.shouldShowDailyReminder()) {
            return
        }

        val openedCount = prefsManager.getOpenedGuidesCount()
        val totalCount = prefsManager.totalGuidesCount
        val progress = prefsManager.getLearningProgress()

        val message = if (openedCount == 0) {
            "You haven't explored any first aid guides yet!\n\n" +
                    "📖 Start learning today - every minute counts in an emergency.\n\n" +
                    "Tap a guide on the home screen to begin."
        } else if (openedCount < totalCount) {
            "Great progress! You've explored $openedCount out of $totalCount guides ($progress%).\n\n" +
                    "📚 Continue your learning journey today.\n\n" +
                    "Knowledge is power in emergencies!"
        } else {
            "🎉 Amazing! You've explored all $totalCount guides!\n\n" +
                    "💪 Keep reviewing to stay sharp.\n\n" +
                    "Consider watching educational videos for deeper understanding."
        }

        AlertDialog.Builder(context)
            .setTitle("Daily Learning Reminder 📚")
            .setMessage(message)
            .setPositiveButton("Start Learning") { dialog, _ ->
                notificationManager.markReminderShownToday()
                dialog.dismiss()
                // Navigate to home screen to browse guides
                onStartLearning()
            }
            .setNeutralButton("Remind Me Later") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Don't Show Again") { dialog, _ ->
                prefsManager.dontShowDailyReminder = true
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show progress dialog when user completes reading all guides
     */
    fun showCompletionDialog() {
        AlertDialog.Builder(context)
            .setTitle("🎉 Congratulations!")
            .setMessage(
                "You've explored all first aid guides!\n\n" +
                        "You're now better prepared to handle emergencies.\n\n" +
                        "📺 Want to deepen your knowledge?\n" +
                        "Check out video tutorials for each procedure.\n\n" +
                        "💡 Tip: Regular review helps retain critical information."
            )
            .setPositiveButton("Great!") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show settings dialog for learning reminders
     */
    fun showReminderSettingsDialog() {
        val currentlyEnabled = prefsManager.learningRemindersEnabled
        val currentFrequency = prefsManager.notificationFrequency

        val frequencyText = when (currentFrequency) {
            "daily" -> "Daily"
            "every_2_days" -> "Every 2 Days"
            "weekly" -> "Weekly"
            else -> "Daily"
        }

        val message = "Current Status: ${if (currentlyEnabled) "Enabled" else "Disabled"}\n" +
                "Frequency: $frequencyText\n" +
                "Time: ${prefsManager.notificationTimeHour}:00\n\n" +
                "What would you like to do?"

        AlertDialog.Builder(context)
            .setTitle("Learning Reminder Settings")
            .setMessage(message)
            .setPositiveButton(if (currentlyEnabled) "Disable" else "Enable") { dialog, _ ->
                prefsManager.learningRemindersEnabled = !currentlyEnabled
                val notificationManager = LearningNotificationManager(context)

                if (prefsManager.learningRemindersEnabled) {
                    notificationManager.scheduleDailyNotifications()
                } else {
                    notificationManager.cancelScheduledNotifications()
                }

                dialog.dismiss()
            }
            .setNeutralButton("Change Frequency") { dialog, _ ->
                dialog.dismiss()
                showNotificationFrequencyDialog {}
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}

