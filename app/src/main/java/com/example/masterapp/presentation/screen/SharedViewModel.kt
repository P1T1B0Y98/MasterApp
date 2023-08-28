package com.example.masterapp.presentation.screen

import UserProfile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.masterapp.data.Assessment
import com.example.masterapp.data.HealthConnectManager


class SharedViewModel(
) : ViewModel(
) {
    // LiveData to hold the assessment data
    private val _assessmentData = MutableLiveData<Assessment?>()

    private val _userData = MutableLiveData<UserProfile?>()


    // Function to set the assessment data
    fun setAssessment(assessment: Assessment?) {
        _assessmentData.value = assessment
    }

    fun getAssessment(): Assessment? {
        return _assessmentData.value
    }

    fun setUserProfile(userProfile: UserProfile) {
        _userData.value = userProfile
    }

    fun getUserProfile(): UserProfile? {
        return AuthManager.getUserProfile()
    }


}
