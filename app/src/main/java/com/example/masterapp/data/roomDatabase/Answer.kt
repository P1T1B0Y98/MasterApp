package com.example.masterapp.data.roomDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "answers")
data class Answer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "assessment_id") val assessmentId: String,
    @ColumnInfo(name = "assessment_title") val assessmentTitle: String,
    @ColumnInfo(name = "assessment_type") val assessmentType: String,
    @ColumnInfo(name = "timestamp") val timestamp: String,
    @TypeConverters(QuestionAnswerConverter::class)
    @ColumnInfo(name = "question_answers") val questionAnswers: List<QuestionAnswer>
)

data class QuestionAnswer(
    val questionId: Int,
    val type: String,
    val question: String,)

