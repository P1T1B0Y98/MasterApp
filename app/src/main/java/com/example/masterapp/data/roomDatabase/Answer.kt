package com.example.masterapp.data.roomDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.masterapp.data.AnswerData
import com.example.masterapp.data.DynamicAnswerData
import com.example.masterapp.data.ExerciseSession
import com.example.masterapp.data.HeartRateData
import com.example.masterapp.data.HeartRateVariabilityData
import com.example.masterapp.data.SleepSessionData

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
    val answers: List<DynamicAnswerData>               // hrv answer
)

