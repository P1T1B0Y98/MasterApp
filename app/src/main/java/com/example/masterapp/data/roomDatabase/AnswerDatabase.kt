package com.example.masterapp.data.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Answer::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(QuestionAnswerConverter::class)

abstract class AnswerDatabase : RoomDatabase() {
    abstract val answerDao: AnswerDao

    companion object {
        @Volatile
        private var INSTANCE: AnswerDatabase? = null

        fun getDatabase(context: Context): AnswerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnswerDatabase::class.java,
                    "answer_database"
                )
                    .fallbackToDestructiveMigration() // Add this to allow destructive migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}