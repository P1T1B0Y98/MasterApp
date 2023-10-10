package com.example.masterapp.data.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [QuestionnaireReminder::class], // Added QuestionnaireStatus::class
    version = 4,
    exportSchema = false
)
@TypeConverters(QuestionAnswerConverter::class)
abstract class QuestionnaireReminderDatabase : RoomDatabase() {
    abstract val questionnaireReminderDao: QuestionnaireReminderDao // Added this DAO reference

    companion object {
        @Volatile
        private var INSTANCE: QuestionnaireReminderDatabase? = null

        fun getDatabase(context: Context): QuestionnaireReminderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestionnaireReminderDatabase::class.java,
                    "questionnaire_reminder_database"
                )
                    .fallbackToDestructiveMigration() // Add this to allow destructive migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
