package com.example.firstaidapp.data.models

data class GuideStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val imageResName: String? = null,
    val duration: String? = null, // e.g., "30 seconds", "2 minutes"
    val isCompleted: Boolean = false
)
