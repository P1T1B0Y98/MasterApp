package com.example.masterapp.data.roomDatabase

class QuestionnaireRepository(private val dao: QuestionnaireReminderDao) {

    suspend fun insertReminder(reminder: QuestionnaireReminder) {
        dao.insertReminder(reminder)
    }

    suspend fun getQuestionnaireReminders(userId: String): List<QuestionnaireReminder> {
        return dao.getRemindersForUser(userId)
    }

    suspend fun getQuestionnaire(userId: String, questionnaireId: String): QuestionnaireReminder {
        return dao.getQuestionnaire(userId, questionnaireId)
    }
}
