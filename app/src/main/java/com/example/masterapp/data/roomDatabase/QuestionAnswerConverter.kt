package com.example.masterapp.data.roomDatabase

import androidx.room.TypeConverter
import com.example.masterapp.data.roomDatabase.QuestionAnswer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class QuestionAnswerConverter {

    @TypeConverter
    fun fromQuestionAnswerList(questionAnswers: List<QuestionAnswer>): String {
        return Gson().toJson(questionAnswers)
    }

    @TypeConverter
    fun toQuestionAnswerList(questionAnswersString: String): List<QuestionAnswer> {
        val type = object: TypeToken<List<QuestionAnswer>>() {}.type
        return Gson().fromJson(questionAnswersString, type)
    }
}
