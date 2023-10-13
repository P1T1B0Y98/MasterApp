package com.example.masterapp.data

import android.content.Context
import androidx.core.content.edit

class UserPreferences(context: Context) {

    private val appContext = context.applicationContext
    private val preferenceKey = "UserPreferences"
    private val sharedPreferences = appContext.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_USER_ID = "id"
        private const val PREF_AUTHENTICATION_UID = "authenticationUid"
        private const val PREF_FULL_NAME = "fullName"
        private const val PREF_FIRST_NAME = "firstName"
        private const val PREF_EMAIL = "email"
    }

    fun saveUserInformation(id: String, authenticationUid: String, fullName: String, firstName: String, email: String) {
        sharedPreferences.edit {
            putString(PREF_USER_ID, id)
            putString(PREF_AUTHENTICATION_UID, authenticationUid)
            putString(PREF_FULL_NAME, fullName)
            putString(PREF_FIRST_NAME, firstName)
            putString(PREF_EMAIL, email)
            apply()
        }
    }

    fun getUserInformation(): UserInformation? {
        val id = sharedPreferences.getString(PREF_USER_ID, null)
        val authenticationUid = sharedPreferences.getString(PREF_AUTHENTICATION_UID, null)
        val fullName = sharedPreferences.getString(PREF_FULL_NAME, null)
        val firstName = sharedPreferences.getString(PREF_FIRST_NAME, null)
        val email = sharedPreferences.getString(PREF_EMAIL, null)

        return if (id != null && authenticationUid != null && fullName != null && firstName != null && email != null) {
            UserInformation(id, authenticationUid, fullName, firstName, email)
        } else {
            null
        }
    }

    data class UserInformation(
        val id: String,
        val authenticationUid: String,
        val fullName: String,
        val firstName: String,
        val email: String
    )
}
