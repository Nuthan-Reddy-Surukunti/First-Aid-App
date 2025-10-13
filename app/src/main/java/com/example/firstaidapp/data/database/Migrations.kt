package com.example.firstaidapp.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE emergency_contacts ADD COLUMN relationship TEXT")
        database.execSQL("ALTER TABLE emergency_contacts ADD COLUMN notes TEXT")
    }
}
