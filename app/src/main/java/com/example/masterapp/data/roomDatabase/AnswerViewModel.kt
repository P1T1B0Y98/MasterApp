package com.example.masterapp.data.roomDatabase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AnswerViewModel(private val questionnaireReminderDao: QuestionnaireReminderDao): ViewModel() {

}
class AnswerViewModelFactory(private val questionnaireReminderDao: QuestionnaireReminderDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnswerViewModel::class.java)) {
            return AnswerViewModel(questionnaireReminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



