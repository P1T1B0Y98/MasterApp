package com.example.masterapp.presentation.screen.setup

import android.content.Context

class PreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun setSetupComplete(completed: Boolean) {
        sharedPreferences.edit().putBoolean("setup_complete", completed).apply()
    }

    fun isSetupComplete(): Boolean {
        return sharedPreferences.getBoolean("setup_complete", false)
    }
}
