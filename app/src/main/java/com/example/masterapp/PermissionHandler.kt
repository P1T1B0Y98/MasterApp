package com.example.masterapp

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.os.Build
import android.content.SharedPreferences

class NotificationSettingsManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("notification_preferences", Context.MODE_PRIVATE)

    /**
     * Check if the app has permission to show notifications.
     * Specifically, on Android O and above, checks if the notification channel "REMINDER_CHANNEL_ID"
     * has been silenced by the user.
     */
    fun canShowNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return true
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel("REMINDER_CHANNEL_ID")
        return channel?.importance != NotificationManager.IMPORTANCE_NONE
    }

    /**
     * Opens the app's notification settings page.
     */
    fun openNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Save user preference related to some notification setting.
     */
    fun saveUserPreference(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    /**
     * Retrieve user preference related to some notification setting.
     */
    fun getUserPreference(key: String, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }
}
