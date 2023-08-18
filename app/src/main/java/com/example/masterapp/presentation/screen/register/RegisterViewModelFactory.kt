package com.example.masterapp.presentation.screen.register

import com.example.masterapp.presentation.screen.register.RegisterViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollographql.apollo3.ApolloClient



class RegisterViewModelFactory(
    private val apolloClient: ApolloClient,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(apolloClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}