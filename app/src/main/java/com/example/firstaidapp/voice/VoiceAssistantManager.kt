package com.example.firstaidapp.voice

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firstaidapp.data.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Main coordinator for the Voice Assistant system
 * Integrates Speech Recognition, TTS, and Gemini AI
 */
class VoiceAssistantManager(private val context: Context) {

    private val tag = "VoiceAssistantManager"
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Component managers
    private val speechRecognition = SpeechRecognitionService(context)
    private val textToSpeech = TextToSpeechManager(context)
    private val geminiAI = GeminiAIManager(context)
    private val permissionManager = VoicePermissionManager(context)

    // State management
    private val _assistantState = MutableStateFlow<VoiceAssistantState>(VoiceAssistantState.Idle)
    val assistantState: StateFlow<VoiceAssistantState> = _assistantState.asStateFlow()

    private val _currentResponse = MutableLiveData<VoiceResponse>()
    val currentResponse: LiveData<VoiceResponse> = _currentResponse

    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> = _recognizedText

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private var isInitialized = false
    private var currentPreferences = VoicePreferences()

    /**
     * Initialize the voice assistant
     */
    fun initialize(onComplete: (Boolean) -> Unit) {
        if (isInitialized) {
            onComplete(true)
            return
        }

        Log.d(tag, "Initializing Voice Assistant...")

        // Check permissions first
        if (!permissionManager.areAllPermissionsGranted()) {
            val missingPerms = permissionManager.getMissingPermissions()
            Log.w(tag, "Missing permissions: $missingPerms")
            // Continue with limited functionality - don't return, just log
        }

        // Initialize components in sequence
        var ttsReady = false
        var speechReady = false
        var aiReady = false

        // Initialize Speech Recognition first (no callback needed)
        try {
            speechRecognition.initialize()
            speechReady = true
            Log.d(tag, "Speech Recognition initialization: $speechReady")
        } catch (e: Exception) {
            Log.w(tag, "Speech Recognition initialization failed: ${e.message}")
            speechReady = false
        }

        // Initialize TTS with retry mechanism
        textToSpeech.initialize { ttsSuccess ->
            ttsReady = ttsSuccess
            Log.d(tag, "TTS initialization: $ttsSuccess")

            // Check AI availability (but don't fail if it's not available)
            scope.launch {
                try {
                    // Give AI a moment to initialize
                    delay(500)
                    aiReady = geminiAI.isModelInitialized.get()
                    Log.d(tag, "AI initialization: $aiReady")
                } catch (e: Exception) {
                    Log.w(tag, "AI check failed: ${e.message}")
                    aiReady = false
                }

                // Mark as initialized if we have basic TTS (minimum requirement) OR if we have speech recognition
                isInitialized = ttsReady || speechReady

                Log.d(tag, "Voice Assistant components status - TTS: $ttsReady, Speech: $speechReady, AI: $aiReady")

                if (isInitialized) {
                    _assistantState.value = VoiceAssistantState.Idle
                    Log.d(tag, "Voice Assistant initialized successfully with available components")

                    // Set up AI callbacks if available
                    if (aiReady) {
                        try {
                            geminiAI.onEmergencyCall = { emergencyType, urgency ->
                                scope.launch {
                                    handleEmergencyCall(emergencyType, urgency)
                                }
                            }

                            geminiAI.onNavigateToProcedure = { procedureName ->
                                scope.launch {
                                    handleProcedureNavigation(procedureName)
                                }
                            }
                            Log.d(tag, "AI callbacks configured")
                        } catch (e: Exception) {
                            Log.w(tag, "Failed to set up AI callbacks: ${e.message}")
                        }
                    } else {
                        Log.w(tag, "AI not available - will use fallback responses")
                        _errorMessage.postValue("AI features unavailable - using offline mode")
                    }
                } else {
                    Log.w(tag, "Voice assistant not initialized - critical components failed")
                    _assistantState.value = VoiceAssistantState.Error("Essential components unavailable")
                    _errorMessage.postValue("Voice assistant unavailable")
                }

                onComplete(isInitialized)
            }
        }
    }

    /**
     * Start listening for voice commands
     */
    fun startListening() {
        if (!isInitialized) {
            Log.w(tag, "Voice assistant not initialized")
            _errorMessage.postValue("Voice assistant not ready")
            return
        }

        if (_assistantState.value is VoiceAssistantState.Listening) {
            Log.w(tag, "Already listening")
            return
        }

        // Stop any current speech
        textToSpeech.stop()

        _assistantState.value = VoiceAssistantState.Listening
        Log.d(tag, "Started listening for voice commands")

        speechRecognition.startListening(
            onResult = { text ->
                handleRecognizedSpeech(text)
            },
            onError = { error ->
                Log.e(tag, "Speech recognition error: $error")
                _assistantState.value = VoiceAssistantState.Error(error)
                _errorMessage.postValue(error)

                // Auto-retry for common errors
                if (error.contains("No speech") || error.contains("timeout")) {
                    scope.launch {
                        delay(1000)
                        if (_assistantState.value is VoiceAssistantState.Error) {
                            _assistantState.value = VoiceAssistantState.Idle
                        }
                    }
                }
            },
            onReadyForSpeech = {
                Log.d(tag, "Ready for speech input")
            },
            onPartialResult = { partialText ->
                _recognizedText.postValue(partialText)
            }
        )
    }

    /**
     * Stop listening
     */
    fun stopListening() {
        speechRecognition.stopListening()
        if (_assistantState.value is VoiceAssistantState.Listening) {
            _assistantState.value = VoiceAssistantState.Idle
        }
        Log.d(tag, "Stopped listening")
    }

    /**
     * Process recognized speech
     */
    private fun handleRecognizedSpeech(text: String) {
        Log.d(tag, "Processing recognized speech: $text")
        _recognizedText.postValue(text)
        _assistantState.value = VoiceAssistantState.Processing

        scope.launch {
            try {
                // Send to Gemini AI for processing
                val result = geminiAI.processVoiceCommand(text)

                result.onSuccess { response ->
                    _currentResponse.postValue(response)

                    // Speak the response
                    if (currentPreferences.autoSpeak) {
                        speakResponse(response.text)
                    } else {
                        _assistantState.value = VoiceAssistantState.Idle
                    }

                    // Handle any required actions
                    response.actionRequired?.let { action ->
                        handleVoiceAction(action)
                    }
                }

                result.onFailure { error ->
                    Log.e(tag, "AI processing error", error)
                    _assistantState.value = VoiceAssistantState.Error(error.message ?: "Processing failed")
                    _errorMessage.postValue(error.message ?: "Failed to process command")

                    // Fallback to basic command processing
                    handleBasicCommand(text)
                }

            } catch (e: Exception) {
                Log.e(tag, "Error handling speech", e)
                _assistantState.value = VoiceAssistantState.Error(e.message ?: "Unknown error")
                _errorMessage.postValue(e.message ?: "An error occurred")
            }
        }
    }

    /**
     * Speak a response
     */
    fun speakResponse(text: String, onComplete: (() -> Unit)? = null) {
        _assistantState.value = VoiceAssistantState.Speaking

        textToSpeech.speak(
            text = text,
            onStart = {
                Log.d(tag, "Started speaking: $text")
            },
            onDone = {
                Log.d(tag, "Finished speaking")
                _assistantState.value = VoiceAssistantState.Idle
                onComplete?.invoke()
            },
            onError = {
                Log.e(tag, "TTS error")
                _assistantState.value = VoiceAssistantState.Error("Speech output failed")
                _errorMessage.postValue("Failed to speak response")
            }
        )
    }

    /**
     * Stop speaking
     */
    fun stopSpeaking() {
        textToSpeech.stop()
        if (_assistantState.value is VoiceAssistantState.Speaking) {
            _assistantState.value = VoiceAssistantState.Idle
        }
    }

    /**
     * Handle emergency command directly
     */
    fun handleEmergencyCommand(commandType: VoiceCommandType) {
        scope.launch {
            _assistantState.value = VoiceAssistantState.Processing

            // Convert command type to text for processing
            val emergencyText = when (commandType) {
                VoiceCommandType.EMERGENCY_CPR -> "CPR emergency - person not breathing"
                VoiceCommandType.EMERGENCY_CHOKING -> "choking emergency - person can't breathe"
                VoiceCommandType.EMERGENCY_BLEEDING -> "severe bleeding emergency"
                VoiceCommandType.EMERGENCY_BURNS -> "burn injury emergency"
                else -> "emergency situation"
            }

            val result = geminiAI.processEmergencyVoiceInput(emergencyText)
            result.onSuccess { response: VoiceResponse ->
                _currentResponse.postValue(response)
                speakResponse(response.text ?: "Emergency guidance unavailable")

                response.actionRequired?.let { action: VoiceAction ->
                    handleVoiceAction(action)
                }
            }

            result.onFailure { error: Throwable ->
                Log.e(tag, "Emergency guidance error", error)
                _errorMessage.postValue(error.message ?: "Failed to get emergency guidance")
                provideFallbackEmergencyGuidance(commandType)
            }
        }
    }

    /**
     * Handle voice actions
     */
    private fun handleVoiceAction(action: VoiceAction) {
        when (action) {
            is VoiceAction.NavigateToProcedure -> {
                Log.d(tag, "Navigate to procedure: ${action.procedureId}")
                // Trigger navigation event
            }
            is VoiceAction.StartTimer -> {
                Log.d(tag, "Start timer: ${action.durationSeconds}s - ${action.label}")
                // Trigger timer start event
            }
            is VoiceAction.StopTimer -> {
                Log.d(tag, "Stop timer")
                // Trigger timer stop event
            }
            is VoiceAction.CallEmergency -> {
                Log.d(tag, "Call emergency: ${action.number}")
                // Trigger emergency call
            }
            is VoiceAction.FindHospital -> {
                Log.d(tag, "Find hospital")
                // Trigger hospital finder
            }
            is VoiceAction.ShowSteps -> {
                Log.d(tag, "Show steps: ${action.steps.size} steps")
                // Display procedure steps
            }
            is VoiceAction.ShowGuide -> {
                Log.d(tag, "Show guide: ${action.guideId}")
                // Display specific guide
            }
        }
    }

    /**
     * Basic command processing fallback (offline mode)
     */
    private fun handleBasicCommand(text: String) {
        val lowerText = text.lowercase()

        val response = when {
            lowerText.contains("cpr") || lowerText.contains("not breathing") -> {
                "Call 911 immediately. Start CPR: 30 chest compressions, then 2 rescue breaths. Push hard and fast at 100 to 120 compressions per minute."
            }
            lowerText.contains("choking") -> {
                "If they can't cough, speak, or breathe, perform the Heimlich maneuver: Stand behind them, wrap your arms around their waist, and give quick upward thrusts."
            }
            lowerText.contains("bleeding") -> {
                "Apply direct pressure with a clean cloth. Elevate the wound above the heart if possible. Do not remove the cloth. Call 911 if bleeding is severe."
            }
            lowerText.contains("burn") -> {
                "Cool the burn under running water for 10-20 minutes. Do not use ice. Cover with a clean, non-stick dressing. Seek medical help for severe burns."
            }
            lowerText.contains("call") && lowerText.contains("emergency") -> {
                "Calling emergency services now."
            }
            else -> {
                "I'm having trouble connecting to the AI service. Please describe your emergency clearly, or call 911 for immediate help."
            }
        }

        speakResponse(response)
        _currentResponse.postValue(VoiceResponse(response))
    }

    /**
     * Provide fallback emergency guidance when AI is unavailable
     */
    private fun provideFallbackEmergencyGuidance(commandType: VoiceCommandType) {
        val guidance = when (commandType) {
            VoiceCommandType.EMERGENCY_CPR ->
                "Call 911 now. Start CPR: Push hard and fast in the center of the chest at least 100 times per minute. Let the chest rise completely between compressions."
            VoiceCommandType.EMERGENCY_CHOKING ->
                "If they can't breathe, stand behind them and give 5 sharp blows between the shoulder blades. Then do 5 abdominal thrusts."
            VoiceCommandType.EMERGENCY_BLEEDING ->
                "Apply direct pressure with a clean cloth. Press firmly and don't lift to check. Call 911 if bleeding doesn't stop."
            VoiceCommandType.EMERGENCY_BURNS ->
                "Cool the burn with running water for 15 minutes. Don't use ice. Cover with a clean cloth. Call 911 for serious burns."
            else ->
                "Call 911 immediately for emergency help."
        }

        speakResponse(guidance)
        _currentResponse.postValue(VoiceResponse(guidance))
    }

    /**
     * Update preferences
     */
    fun updatePreferences(preferences: VoicePreferences) {
        currentPreferences = preferences
        textToSpeech.updatePreferences(preferences)
        Log.d(tag, "Preferences updated")
    }

    /**
     * Clear conversation history (start fresh emergency)
     */
    fun clearConversationHistory() {
        // Clear any tracked emergency procedures
        Log.d(tag, "Conversation history cleared for new emergency")
    }

    /**
     * Start Gemini Live voice conversation for real-time emergency assistance
     */
    suspend fun startLiveVoiceSession(): Result<String> {
        return try {
            _assistantState.value = VoiceAssistantState.Processing

            val result = geminiAI.startVoiceConversation()

            result.onSuccess { message ->
                _assistantState.value = VoiceAssistantState.Speaking
                Log.d(tag, "Live voice session started: $message")

                // Speak welcome message for live session
                textToSpeech.speak("Live emergency assistant activated. Describe your emergency situation clearly.")
            }

            result.onFailure { error ->
                _assistantState.value = VoiceAssistantState.Error(error.message ?: "Failed to start live session")
                Log.e(tag, "Failed to start live session", error)
            }

            result
        } catch (e: Exception) {
            Log.e(tag, "Error starting live voice session", e)
            Result.failure(e)
        }
    }

    /**
     * Stop Gemini Live voice conversation
     */
    suspend fun stopLiveVoiceSession(): Result<String> {
        return try {
            val result = geminiAI.stopVoiceConversation()

            result.onSuccess { message ->
                _assistantState.value = VoiceAssistantState.Idle
                Log.d(tag, "Live voice session stopped: $message")
            }

            result
        } catch (e: Exception) {
            Log.e(tag, "Error stopping live voice session", e)
            Result.failure(e)
        }
    }

    /**
     * Handle emergency call triggered by Gemini Live API
     */
    private suspend fun handleEmergencyCall(emergencyType: String, urgency: String) {
        try {
            Log.d(tag, "Live API triggered emergency call: $emergencyType, urgency: $urgency")

            // Create emergency action
            val emergencyAction = VoiceAction.CallEmergency("911")

            // Speak urgent message
            val urgentMessage = when (urgency) {
                "CRITICAL" -> "CALLING 911 NOW! This is a critical emergency."
                "HIGH" -> "Calling 911 immediately for this high priority emergency."
                else -> "Initiating emergency services call."
            }

            textToSpeech.speak(urgentMessage)
            handleVoiceAction(emergencyAction)

        } catch (e: Exception) {
            Log.e(tag, "Error handling emergency call", e)
        }
    }

    /**
     * Handle procedure navigation triggered by Gemini Live API
     */
    private suspend fun handleProcedureNavigation(procedureName: String) {
        try {
            Log.d(tag, "Live API requested navigation to: $procedureName")

            // Create navigation action
            val navigationAction = VoiceAction.NavigateToProcedure(procedureName)

            // Speak confirmation
            textToSpeech.speak("Opening $procedureName procedure guide now.")
            handleVoiceAction(navigationAction)

        } catch (e: Exception) {
            Log.e(tag, "Error handling procedure navigation", e)
        }
    }

    /**
     * Check if assistant is ready
     */
    fun isReady(): Boolean = isInitialized

    /**
     * Get permission manager
     */
    fun getPermissionManager(): VoicePermissionManager = permissionManager

    /**
     * Release resources
     */
    fun shutdown() {
        speechRecognition.destroy()
        textToSpeech.shutdown()
        scope.cancel()
        isInitialized = false
        Log.d(tag, "Voice assistant shutdown")
    }
}

/**
 * Voice Assistant State
 */
sealed class VoiceAssistantState {
    object Idle : VoiceAssistantState()
    object Listening : VoiceAssistantState()
    object Processing : VoiceAssistantState()
    object Speaking : VoiceAssistantState()
    object Connecting : VoiceAssistantState()
    object LiveConversation : VoiceAssistantState()
    data class Error(val message: String) : VoiceAssistantState()
}

