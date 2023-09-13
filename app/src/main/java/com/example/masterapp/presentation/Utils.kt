package com.example.masterapp.presentation

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatDateAndTime(dateTimeString: String): String {
    val originalFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val dateOutputFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
    val timeOutputFormat = DateTimeFormatter.ofPattern("hh:mm a")

    val dateTime = ZonedDateTime.parse(dateTimeString, originalFormat)

    val dateFormatted = dateOutputFormat.format(dateTime)
    val timeFormatted = timeOutputFormat.format(dateTime)

    return "$dateFormatted at $timeFormatted"
}

fun formatDate(dateTimeString: String): String {
    val originalFormat = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val dateOutputFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy")

    val dateTime = ZonedDateTime.parse(dateTimeString, originalFormat)

    return dateOutputFormat.format(dateTime)
}

fun formatTimeOnly(dateTimeString: String): String {
    val originalFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")
    val timeOutputFormat = DateTimeFormatter.ofPattern("HH:mm")

    val dateTime = ZonedDateTime.parse(dateTimeString, originalFormat)

    return timeOutputFormat.format(dateTime)
}

fun formatTimeAdjusted(dateTimeString: String): String {
    val originalFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

    val dateTime = ZonedDateTime.parse(dateTimeString, originalFormat)

    // Calculate the new adjusted time
    val adjustedDateTime = dateTime.plusSeconds(dateTime.offset.totalSeconds.toLong())

    val timeOutputFormat = DateTimeFormatter.ofPattern("HH:mm")
    return timeOutputFormat.format(adjustedDateTime)
}


fun formatDuration(duration: java.time.Duration): String {
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60  // This will give the remaining minutes after hours

    return "${hours}h ${minutes}min"
}

fun Double.roundToTwoDecimals(): String {
    return String.format("%.2f", this)
}


