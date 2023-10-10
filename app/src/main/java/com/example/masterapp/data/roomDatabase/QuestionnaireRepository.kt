package com.example.masterapp.data.roomDatabase

class QuestionnaireRepository(private val dao: QuestionnaireReminderDao) {

    suspend fun insertReminder(reminder: QuestionnaireReminder) {
        dao.insertReminder(reminder)
    }

    suspend fun deleteReminder(userId: String, questionnaireId: String) {
        dao.deleteReminder(userId, questionnaireId)
    }

    suspend fun updateReminder(reminder: QuestionnaireReminder) {
        dao.updateReminder(reminder)
    }
    suspend fun deleteAllReminders(userId: String) {
        dao.deleteAllReminders(userId)
    }

    suspend fun getQuestionnaireReminders(userId: String): List<QuestionnaireReminder> {
        return dao.getRemindersForUser(userId)
    }

    suspend fun getQuestionnaire(userId: String, questionnaireId: String): QuestionnaireReminder {
        return dao.getQuestionnaire(userId, questionnaireId)
    }
}
