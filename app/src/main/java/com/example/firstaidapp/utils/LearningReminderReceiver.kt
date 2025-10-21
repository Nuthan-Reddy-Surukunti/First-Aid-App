package com.example.firstaidapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver for learning reminder notifications
 * Triggered by AlarmManager to show daily learning reminders
 */
class LearningReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = LearningNotificationManager(context)
        notificationManager.showLearningNotification()
    }
}
