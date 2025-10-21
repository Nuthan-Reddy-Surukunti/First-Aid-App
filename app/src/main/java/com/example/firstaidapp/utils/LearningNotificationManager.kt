package com.example.firstaidapp.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.firstaidapp.MainActivity
import com.example.firstaidapp.R
import java.util.Calendar

/**
 * Manages learning reminder notifications for the First Aid app
 * Sends daily notifications to encourage users to learn first aid procedures
 */
class LearningNotificationManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val prefsManager = UserPreferencesManager(context)

    companion object {
        private const val CHANNEL_ID = "learning_reminders"
        private const val CHANNEL_NAME = "Learning Reminders"
        private const val CHANNEL_DESCRIPTION = "Daily reminders to learn first aid procedures"
        private const val NOTIFICATION_ID = 1001
        private const val ALARM_REQUEST_CODE = 2001

        // Learning topics for rotating notifications
        private val LEARNING_TOPICS = listOf(
            "Learn CPR today - It could save a life! 🚑",
            "Do you know what to do for choking? Learn now! 🆘",
            "Master bleeding control techniques today 🩹",
            "Learn burn treatment procedures 🔥",
            "Bone fractures: Know the signs and first aid 🦴",
            "Poisoning emergencies: Be prepared! ☠️",
            "Recognize and treat shock - Critical knowledge! ⚡",
            "Heart attack signs: Every second counts! ❤️",
            "Stroke awareness: Act F.A.S.T. 🧠",
            "Allergic reactions: Learn to identify and help! 🐝"
        )
    }

    init {
        createNotificationChannel()
    }

    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Schedule daily notifications based on user preferences
     */
    fun scheduleDailyNotifications() {
        if (!prefsManager.learningRemindersEnabled) {
            cancelScheduledNotifications()
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, LearningReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate next notification time
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, prefsManager.notificationTimeHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Schedule repeating alarm based on frequency
        val intervalMillis = when (prefsManager.notificationFrequency) {
            "daily" -> AlarmManager.INTERVAL_DAY
            "every_2_days" -> AlarmManager.INTERVAL_DAY * 2
            "weekly" -> AlarmManager.INTERVAL_DAY * 7
            else -> AlarmManager.INTERVAL_DAY
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            intervalMillis,
            pendingIntent
        )
    }

    /**
     * Cancel scheduled notifications
     */
    fun cancelScheduledNotifications() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, LearningReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Show a learning reminder notification
     */
    fun showLearningNotification() {
        val openedGuidesCount = prefsManager.getOpenedGuidesCount()
        val totalGuides = prefsManager.totalGuidesCount
        val progress = prefsManager.getLearningProgress()

        // Select a random learning topic
        val topic = LEARNING_TOPICS.random()

        // Create intent to open the app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Using system icon temporarily
            .setContentTitle("First Aid Learning Reminder")
            .setContentText(topic)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$topic\n\nYour progress: $openedGuidesCount/$totalGuides guides explored ($progress%)"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Check if we should show in-app reminder today
     */
    fun shouldShowDailyReminder(): Boolean {
        if (prefsManager.dontShowDailyReminder) return false
        if (!prefsManager.learningRemindersEnabled) return false

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis.toString()

        return prefsManager.lastReminderDate != today
    }

    /**
     * Mark that we've shown the reminder today
     */
    fun markReminderShownToday() {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis.toString()

        prefsManager.lastReminderDate = today
    }
}
