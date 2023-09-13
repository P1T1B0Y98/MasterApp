package com.example.masterapp.data

import android.graphics.drawable.Drawable
import kotlinx.serialization.Serializable

/**
 * Information about an app that can be used for displaying attribution.
 */
@Serializable
data class MasterAppInfo(
    val packageName: String,
    val appLabel: String,
)