package com.example.masterapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//Represents
@Parcelize
data class UserProfile(
    val name: String,
) : Parcelable
