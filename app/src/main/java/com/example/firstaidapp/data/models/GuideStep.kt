package com.example.firstaidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guide_steps")
data class GuideStep(
    @PrimaryKey
    val id: String,
    val guideId: String,
    val stepNumber: Int,
    val title: String,
    val description: String,
    val stepType: StepType = StepType.ACTION,
    val isCritical: Boolean = false,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val duration: String? = null, // Duration as text (e.g., "10-15 seconds")
    val detailedInstructions: String? = null, // Detailed instructions
    val iconRes: Int? = null, // Icon resource ID
    val imageRes: Int? = null, // Image resource ID for step illustration
    val tips: List<String>? = null, // Helpful tips
    val warnings: List<String>? = null, // Warnings
    val requiredTools: List<String>? = null // Required tools/materials
)
