package com.example.masterapp.data.roomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface QuestionnaireReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: QuestionnaireReminder)

    @Query("SELECT * FROM questionnaire_reminders WHERE user_id = :userId")
    suspend fun getRemindersForUser(userId: String): List<QuestionnaireReminder>

    @Update
    suspend fun updateReminder(reminder: QuestionnaireReminder)

    @Query("SELECT * FROM questionnaire_reminders WHERE user_id = :userId AND questionnaire_id = :questionnaireId")
    suspend fun getQuestionnaire(userId: String, questionnaireId: String): QuestionnaireReminder

    @Query("DELETE FROM questionnaire_reminders WHERE user_id = :userId AND questionnaire_id = :questionnaireId")
    suspend fun deleteReminder(userId: String, questionnaireId: String)

    @Query("DELETE FROM questionnaire_reminders WHERE user_id = :userId")
    suspend fun deleteAllReminders(userId: String)
}
