package com.example.masterapp.data.roomDatabase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.masterapp.data.roomDatabase.Answer
import com.example.masterapp.data.roomDatabase.AnswerDao
import kotlinx.coroutines.launch

class AnswerViewModel(private val answerDao: AnswerDao): ViewModel() {

    val answersByUser: MutableLiveData<List<Answer>> = MutableLiveData()
    private val answersByAssessment: MutableLiveData<List<Answer>> = MutableLiveData()
    private val answersByUserAndAssessment: MutableLiveData<List<Answer>> = MutableLiveData()

    fun getAllAnswersByUser(userId: String) = viewModelScope.launch {
        answersByUser.value = answerDao.getAnswersByUser(userId)
    }

    fun getAllAnswersByAssessment(assessmentId: Int) = viewModelScope.launch {
        answersByAssessment.value = answerDao.getAnswersByAssessment(assessmentId)
    }

    fun getAllAnswersByUserAndAssessment(userId: Int, assessmentId: Int) = viewModelScope.launch {
        answersByUserAndAssessment.value = answerDao.getAnswersByUserAndAssessment(userId, assessmentId)
    }

    fun insertAnswer(answer: Answer) = viewModelScope.launch {
        answerDao.insertAnswer(answer)
    }

    fun updateAnswer(answer: Answer) = viewModelScope.launch {
        answerDao.updateAnswer(answer)
    }

    fun deleteAnswer(answer: Answer) = viewModelScope.launch {
        answerDao.deleteAnswer(answer)
    }
}

class AnswerViewModelFactory(private val answerDao: AnswerDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnswerViewModel::class.java)) {
            return AnswerViewModel(answerDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



