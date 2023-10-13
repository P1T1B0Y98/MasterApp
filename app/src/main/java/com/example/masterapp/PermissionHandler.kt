package com.example.masterapp

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.os.Build
import android.content.SharedPreferences

class NotificationSettingsManager(private val context: Context) {

    /**
     * Check if the app has permission to show notifications.
     * Specifically, on Android O and above, checks if the notification channel "REMINDER_CHANNEL_ID"
     * has been silenced by the user.
     */
    fun canShowNotifications(): Boolean {

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel("REMINDER_CHANNEL_ID")
        return channel?.importance != NotificationManager.IMPORTANCE_NONE
    }

    /**
     * Opens the app's notification settings page.
     */
    fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
