package com.example.firstaidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "first_aid_guides")
data class FirstAidGuide(
    @PrimaryKey
    val id: String,
    val title: String,
    val category: String,
    val severity: String,
    val description: String,
    val steps: List<GuideStep> = emptyList(),
    val iconResName: String? = null,
    val whenToCallEmergency: String? = null,
    val warnings: List<String> = emptyList(),
    val estimatedTimeMinutes: Int = 0,
    val difficulty: String = "Beginner",
    val youtubeLink: String? = null,
    val isFavorite: Boolean = false,
    val lastAccessedTimestamp: Long = 0,
    val viewCount: Int = 0
)
