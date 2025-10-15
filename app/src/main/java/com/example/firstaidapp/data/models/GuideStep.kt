package com.example.firstaidapp.data.models

import androidx.room.TypeConverters
import com.example.firstaidapp.data.database.Converters

@TypeConverters(Converters::class)
data class GuideStep(
    val id: String = "",
    val guideId: String = "",
    val stepNumber: Int,
    val title: String,
    val description: String,
    val detailedInstructions: String? = null,
    val iconRes: Int? = null,
    val imageRes: Int? = null,
    val duration: String? = null,
    val stepType: StepType = StepType.ACTION,
    val isCritical: Boolean = false,
    val requiredTools: List<String>? = null,
    val tips: List<String>? = null,
    val warnings: List<String>? = null,
    val videoUrl: String? = null,
    val isCompleted: Boolean = false,
    val order: Int = stepNumber,
    val estimatedDuration: Int? = null, // in seconds
    val type: String = "action" // Keep for backward compatibility
)
