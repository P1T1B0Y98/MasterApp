package com.example.masterapp.data.roomDatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class QuestionnaireReminderViewModel (
    private val repository: QuestionnaireRepository): ViewModel() {

    fun insertReminder(reminder: QuestionnaireReminder) = viewModelScope.launch {
        repository.insertReminder(reminder)
    }

    fun getQuestionnaireReminders(userId: String): LiveData<List<QuestionnaireReminder>> {
        val remindersLiveData = MutableLiveData<List<QuestionnaireReminder>>()
        viewModelScope.launch {
            remindersLiveData.value = repository.getQuestionnaireReminders(userId)
        }
        return remindersLiveData
    }

    fun getQuestionnaireReminder(userId: String, questionnaireId: String): LiveData<QuestionnaireReminder> {
        val reminderLiveData = MutableLiveData<QuestionnaireReminder>()
        viewModelScope.launch {
            reminderLiveData.value = repository.getQuestionnaire(userId, questionnaireId)
        }
        return reminderLiveData
    }

    fun updateQuestionnaireReminder(reminder: QuestionnaireReminder) = viewModelScope.launch {
        repository.updateReminder(reminder)
    }
    fun deleteReminder(userId: String, questionnaireId: String) = viewModelScope.launch {
        repository.deleteReminder(userId, questionnaireId)
    }

    fun deleteAllReminders(userId: String) = viewModelScope.launch {
        repository.deleteAllReminders(userId)
    }
}


class QuestionnaireReminderViewModelFactory(
    private val questionnaireRepository: QuestionnaireRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionnaireReminderViewModel::class.java)) {
            return QuestionnaireReminderViewModel(questionnaireRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}