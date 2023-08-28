package com.example.masterapp.data.roomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuestionnaireReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: QuestionnaireReminder)

    @Query("SELECT * FROM questionnaire_reminders WHERE user_id = :userId")
    suspend fun getRemindersForUser(userId: String): List<QuestionnaireReminder>

    @Query("SELECT * FROM questionnaire_reminders WHERE user_id = :userId AND questionnaire_id = :questionnaireId")
    suspend fun getQuestionnaire(userId: String, questionnaireId: String): QuestionnaireReminder

    // ... Add other necessary queries and operations
}
