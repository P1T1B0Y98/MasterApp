package com.example.masterapp.data.roomDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questionnaire_reminders")
data class QuestionnaireReminder(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "questionnaire_id") val questionnaireId: String,
    @ColumnInfo(name = "notification_timestamp") val notificationTimestamp: Long,
    @ColumnInfo(name = "completed_timestamp") val completedTimestamp: Long? = null
)
