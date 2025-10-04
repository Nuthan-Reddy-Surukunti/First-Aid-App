package com.example.firstaidapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.firstaidapp.data.models.*

@Database(
    entities = [
        FirstAidGuide::class,
        EmergencyContact::class,
        SearchHistory::class
    ],
    version = 2, // Increased version to fix schema mismatch
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun guideDao(): GuideDao
    abstract fun contactDao(): ContactDao
    abstract fun searchDao(): SearchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "first_aid_database"
                )
                    .fallbackToDestructiveMigration() // This will recreate DB on schema change
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
