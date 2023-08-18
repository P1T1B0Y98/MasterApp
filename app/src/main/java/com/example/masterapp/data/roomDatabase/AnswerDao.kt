package com.example.masterapp.data.roomDatabase

import androidx.room.*

@Dao
interface AnswerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: Answer)

    @Update
    suspend fun updateAnswer(answer: Answer)

    @Delete
    suspend fun deleteAnswer(answer: Answer)

    @Query("SELECT * FROM answers WHERE user_id = :userId")
    suspend fun getAnswersByUser(userId: String): List<Answer>

    @Query("SELECT * FROM answers WHERE assessment_id = :assessmentId")
    suspend fun getAnswersByAssessment(assessmentId: Int): List<Answer>

    @Query("SELECT * FROM answers WHERE user_id = :userId AND assessment_id = :assessmentId")
    suspend fun getAnswersByUserAndAssessment(userId: Int, assessmentId: Int): List<Answer>
}
