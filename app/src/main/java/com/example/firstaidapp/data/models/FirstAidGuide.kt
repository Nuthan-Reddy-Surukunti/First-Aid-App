package com.example.firstaidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.firstaidapp.data.database.Converters

@Entity(tableName = "first_aid_guides")
@TypeConverters(Converters::class)
data class FirstAidGuide(
    @PrimaryKey
    val id: String,
    val title: String,
    val category: String,
    val severity: String, // CRITICAL, HIGH, MEDIUM, LOW
    val description: String,
    val steps: List<GuideStep>,
    val iconResName: String, // Required parameter from error
    val whenToCallEmergency: String, // Required parameter from error
    val warnings: List<String> = emptyList(),
    val estimatedTimeMinutes: Int = 0,
    val difficulty: String = "Intermediate", // Beginner, Intermediate, Advanced
    val lastUpdated: Long = System.currentTimeMillis(),

    // Properties expected by GuideDao queries
    val isFavorite: Boolean = false,
    val lastAccessedTimestamp: Long = System.currentTimeMillis(), // Required by GuideDao
    val viewCount: Int = 0,
    val youtubeLink: String = "" // YouTube video demonstration link
)
