package com.example.firstaidapp.data.voice

/**
 * Represents different types of voice commands the assistant can handle
 */
enum class VoiceCommandType {
    EMERGENCY_CPR,
    EMERGENCY_CHOKING,
    EMERGENCY_BLEEDING,
    EMERGENCY_BURNS,
    CALL_EMERGENCY,
    FIND_HOSPITAL,
    START_TIMER,
    STOP_TIMER,
    GENERAL_HELP,
    REPEAT_STEP,
    NEXT_STEP,
    PREVIOUS_STEP,
    UNKNOWN
}

/**
 * Data class for parsed voice commands
 */
data class VoiceCommand(
    val type: VoiceCommandType,
    val originalText: String,
    val confidence: Float = 0f,
    val parameters: Map<String, String> = emptyMap()
)

/**
 * Represents the state of voice recognition
 */
enum class VoiceRecognitionState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

/**
 * Voice assistant response from AI
 */
data class VoiceResponse(
    val text: String,
    val actionRequired: VoiceAction? = null,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Actions that the voice assistant can trigger
 */
sealed class VoiceAction {
    data class NavigateToProcedure(val procedureId: String) : VoiceAction()
    data class StartTimer(val durationSeconds: Int, val label: String) : VoiceAction()
    object StopTimer : VoiceAction()
    data class CallEmergency(val number: String) : VoiceAction()
    data class FindHospital(val latitude: Double, val longitude: Double) : VoiceAction()
    data class ShowSteps(val steps: List<String>) : VoiceAction()
    data class ShowGuide(val guideId: String) : VoiceAction()
}

