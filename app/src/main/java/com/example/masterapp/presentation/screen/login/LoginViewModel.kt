package com.example.masterapp.presentation.screen.login

import UserProfile
import android.util.Log
import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.masterapp.AUTH_MEQuery
import com.example.masterapp.AUTH_SIGN_INMutation

class LoginViewModel(
    private val apolloClient: ApolloClient) : ViewModel() {

    suspend fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        Log.i("Login", "Logging in...")
        print("LoginViewModel.login")
        val response = try {
            apolloClient.mutation(AUTH_SIGN_INMutation(email = email, password = password))
                .execute()
        } catch (e: ApolloException) {
            Log.w("Login", "Failed to login here", e)
            callback(false)
            return
        }
        if (response.hasErrors()) {
            Log.w("Login", "Failed to login: ${response.errors?.get(0)?.message}")
            callback(false)
            return
        }
        Log.i("Respsone", "Response: ${response.data?.result}")
        val token = response.data?.result
        if (token == null) {
            Log.w("Login", "Failed to login: no token returned by the backend")
            callback(false)
            return
        }
        Log.i("Login", "Logged in successfully")
        AuthManager.setToken(token)
        fetchUserProfile()
        callback(true)
    }

    private suspend fun fetchUserProfile(): UserProfile? {
        val response = try {
            apolloClient.query(AUTH_MEQuery()).execute()
        } catch (e: ApolloException) {
            // Handle the error, such as logging or displaying an error message
            null
        }

        val authMe = response?.data?.result
        Log.w("response", response?.data?.result.toString())
        return if (authMe != null) {
            Log.w("Success", "Successfully fetched user profile")
            val userProfile = UserProfile(
                id = authMe.id,
                authenticationUid = authMe.authenticationUid,
                fullName = authMe.fullName,
                firstName = authMe.firstName,
                email = authMe.email
            )
            Log.d("User Profile", "ID: ${userProfile.id}")
            Log.d("User Profile", "Authentication UID: ${userProfile.authenticationUid}")
            Log.d("User Profile", "Full Name: ${userProfile.fullName}")
            Log.d("User Profile", "First Name: ${userProfile.firstName}")
            Log.d("User Profile", "Email: ${userProfile.email}")

            AuthManager.setUserProfile(userProfile)

            userProfile
        } else {
            Log.w("Fail", "Failed to fetch user profile")
            null
        }
    }
}

