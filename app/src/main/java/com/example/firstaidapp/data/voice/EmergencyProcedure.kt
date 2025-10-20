package com.example.firstaidapp.data.voice

/**
 * Emergency procedure data for voice guidance
 */
data class EmergencyProcedure(
    val id: String,
    val name: String,
    val category: String,
    val steps: List<ProcedureStep>,
    val warnings: List<String> = emptyList(),
    val whenToCallEmergency: String = "",
    val estimatedDuration: Int = 0 // in seconds
)

/**
 * Individual step in an emergency procedure
 */
data class ProcedureStep(
    val stepNumber: Int,
    val instruction: String,
    val duration: Int = 0, // in seconds, 0 if no specific duration
    val isRepeatable: Boolean = false,
    val visualAid: String? = null
)

/**
 * Voice preferences for the assistant
 */
data class VoicePreferences(
    val isEnabled: Boolean = true,
    val voiceSpeed: Float = 1.0f, // 0.5 to 2.0
    val voicePitch: Float = 1.0f, // 0.5 to 2.0
    val autoSpeak: Boolean = true,
    val hapticFeedback: Boolean = true,
    val wakeWordEnabled: Boolean = true,
    val language: String = "en-US",
    val offlineMode: Boolean = false
)

