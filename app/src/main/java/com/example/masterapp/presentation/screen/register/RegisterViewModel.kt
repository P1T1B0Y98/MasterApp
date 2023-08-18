package com.example.masterapp.presentation.screen.register

import android.util.Log
import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.masterapp.AUTH_SIGN_UPMutation

class RegisterViewModel(private val apolloClient: ApolloClient) : ViewModel() {

    suspend fun register(email: String, password: String, callback: (Boolean) -> Unit) {
        print("RegisterViewModel.register")
        val response = try {
            apolloClient.mutation(AUTH_SIGN_UPMutation(email = email, password = password))
                .execute()
        } catch (e: ApolloException) {
            Log.w("Register", "Failed to register", e)
            callback(false)
            return
        }
        if (response.hasErrors()) {
            Log.w("Register", "Failed to register: ${response.errors?.get(0)?.message}")
            callback(false)
            return
        }
        val token = response.data?.result
        if (token == null) {
            Log.w("Register", "Failed to register: no token returned by the backend")
            callback(false)
            return
        }
        Log.i("Register", "Registered successfully")
        callback(true)
    }
}
