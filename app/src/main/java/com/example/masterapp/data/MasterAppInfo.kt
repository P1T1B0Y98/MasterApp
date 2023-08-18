package com.example.masterapp.data

import android.graphics.drawable.Drawable

/**
 * Information about an app that can be used for displaying attribution.
 */
data class MasterAppInfo(
    val packageName: String,
    val appLabel: String,
    val icon: Drawable?
)