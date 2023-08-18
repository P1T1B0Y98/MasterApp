package com.example.masterapp.presentation.screen.login

import AuthManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollographql.apollo3.ApolloClient


class LoginViewModelFactory(
    private val apolloClient: ApolloClient,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(apolloClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
