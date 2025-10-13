package com.example.firstaidapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.firstaidapp.data.models.*

@Database(
    entities = [
        FirstAidGuide::class,
        EmergencyContact::class,
        SearchHistory::class
    ],
    version = 5, // Proper migrations applied (3->4, 4->5)
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

        // Migration from 3 to 4: add columns used by queries/UI to first_aid_guides
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add columns with defaults to satisfy NOT NULL constraints
                db.execSQL("ALTER TABLE first_aid_guides ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE first_aid_guides ADD COLUMN lastAccessedTimestamp INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE first_aid_guides ADD COLUMN viewCount INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Migration from 4 to 5: add unique index on emergency_contacts(phoneNumber, type)
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Remove duplicates so unique index can be created safely
                db.execSQL(
                    "DELETE FROM emergency_contacts WHERE id NOT IN (" +
                            "SELECT MIN(id) FROM emergency_contacts GROUP BY phoneNumber, type" +
                            ")"
                )
                // Create the unique index
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_emergency_contacts_phoneNumber_type ON emergency_contacts(phoneNumber, type)"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "first_aid_database"
                )
                    // Replace destructive fallback with proper migrations
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
