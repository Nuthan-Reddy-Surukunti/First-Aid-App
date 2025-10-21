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
        GuideStep::class,
        EmergencyContact::class,
        SearchHistory::class
    ],
    version = 10, // Force database recreation to fix schema issues (9->10)
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

        // Migration from 5 to 6: add youtubeLink column to first_aid_guides
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add youtubeLink column with empty string as default
                db.execSQL("ALTER TABLE first_aid_guides ADD COLUMN youtubeLink TEXT NOT NULL DEFAULT ''")
            }
        }

        // Migration from 6 to 7: add state column to emergency_contacts
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add state column with NULL as default to allow existing data
                db.execSQL("ALTER TABLE emergency_contacts ADD COLUMN state TEXT")
            }
        }

        // Migration from 7 to 8: create guide_steps table
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create guide_steps table (it didn't exist before)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS guide_steps (
                        id TEXT PRIMARY KEY NOT NULL,
                        guideId TEXT NOT NULL,
                        stepNumber INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        stepType TEXT NOT NULL,
                        isCritical INTEGER NOT NULL,
                        imageUrl TEXT,
                        videoUrl TEXT,
                        duration TEXT,
                        detailedInstructions TEXT,
                        iconRes INTEGER,
                        imageRes INTEGER,
                        tips TEXT,
                        warnings TEXT,
                        requiredTools TEXT
                    )
                """.trimIndent())
            }
        }

        // Migration from 8 to 9: fix schema mismatch in first_aid_guides and emergency_contacts
        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Fix first_aid_guides table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS first_aid_guides_new (
                        id TEXT PRIMARY KEY NOT NULL,
                        title TEXT NOT NULL,
                        category TEXT NOT NULL,
                        severity TEXT NOT NULL,
                        description TEXT NOT NULL,
                        steps TEXT NOT NULL,
                        iconResName TEXT,
                        whenToCallEmergency TEXT,
                        warnings TEXT NOT NULL,
                        estimatedTimeMinutes INTEGER NOT NULL,
                        difficulty TEXT NOT NULL,
                        youtubeLink TEXT,
                        isFavorite INTEGER NOT NULL,
                        lastAccessedTimestamp INTEGER NOT NULL,
                        viewCount INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO first_aid_guides_new 
                    (id, title, category, severity, description, steps, iconResName, 
                     whenToCallEmergency, warnings, estimatedTimeMinutes, difficulty, 
                     youtubeLink, isFavorite, lastAccessedTimestamp, viewCount)
                    SELECT id, title, category, severity, description, steps, iconResName,
                           whenToCallEmergency, warnings, estimatedTimeMinutes, difficulty,
                           youtubeLink, isFavorite, lastAccessedTimestamp, viewCount
                    FROM first_aid_guides
                """.trimIndent())

                db.execSQL("DROP TABLE first_aid_guides")
                db.execSQL("ALTER TABLE first_aid_guides_new RENAME TO first_aid_guides")

                // Fix emergency_contacts table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS emergency_contacts_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        phoneNumber TEXT NOT NULL,
                        type TEXT NOT NULL,
                        state TEXT NOT NULL DEFAULT 'National',
                        isDefault INTEGER NOT NULL,
                        description TEXT,
                        relationship TEXT,
                        notes TEXT,
                        isActive INTEGER NOT NULL DEFAULT 1
                    )
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO emergency_contacts_new 
                    (id, name, phoneNumber, type, state, isDefault, description, relationship, notes, isActive)
                    SELECT id, name, phoneNumber, type, 
                           COALESCE(state, 'National'), 
                           isDefault, 
                           NULL,
                           relationship, 
                           notes,
                           1
                    FROM emergency_contacts
                """.trimIndent())

                db.execSQL("DROP TABLE emergency_contacts")
                db.execSQL("ALTER TABLE emergency_contacts_new RENAME TO emergency_contacts")

                // Recreate the unique index
                db.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_emergency_contacts_phoneNumber_type 
                    ON emergency_contacts(phoneNumber, type)
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "first_aid_database"
                )
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                    .fallbackToDestructiveMigration() // Add fallback in case migration fails
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
