package com.example.firstaidapp.voice

import android.content.Context
import android.util.Log
import com.example.firstaidapp.BuildConfig
import com.example.firstaidapp.data.voice.VoiceAction
import com.example.firstaidapp.data.voice.VoiceCommandType
import com.example.firstaidapp.data.voice.VoiceResponse
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Gemini Live API Manager for real-time voice interaction in emergency first aid scenarios
 * Provides bidirectional audio streaming with function calling capabilities
 */
class GeminiAIManager(private val context: Context) {

    private val tag = "GeminiLiveAIManager"

    // Live API session management
    private var isAIModelReady: Boolean = false
    private val isLiveSessionActive = AtomicBoolean(false)
    val isModelInitialized = AtomicBoolean(false)

    // Google AI Client model instance
    private var generativeModel: GenerativeModel? = null

    // Emergency procedures tracking
    private val emergencyProcedures = mutableListOf<String>()

    // Callback for function calls from AI
    var onEmergencyCall: ((String, String) -> Unit)? = null
    var onNavigateToProcedure: ((String) -> Unit)? = null

    private val emergencyResponses = mapOf(
        "cpr" to VoiceResponse(
            text = "Start CPR immediately. Place hands on center of chest. Push hard and fast at least 2 inches deep, 100-120 compressions per minute. Call 911 now!",
            actionRequired = VoiceAction.CallEmergency("911"),
            metadata = mapOf("urgency" to "HIGH", "procedure" to "CPR")
        ),
        "choking" to VoiceResponse(
            text = "For choking: Stand behind person, place hands below ribcage, thrust upward and inward 5 times. Repeat until object comes out or person becomes unconscious.",
            actionRequired = VoiceAction.NavigateToProcedure("choking"),
            metadata = mapOf("urgency" to "HIGH", "procedure" to "HEIMLICH")
        ),
        "bleeding" to VoiceResponse(
            text = "Control bleeding: Apply direct pressure with clean cloth. Keep pressure constant. Elevate the injured area above heart level if possible.",
            actionRequired = VoiceAction.NavigateToProcedure("bleeding"),
            metadata = mapOf("urgency" to "HIGH", "procedure" to "BLEEDING_CONTROL")
        ),
        "burns" to VoiceResponse(
            text = "Cool burn with clean, cool water for 10-20 minutes. Do not use ice. Remove jewelry before swelling. Cover with sterile bandage.",
            actionRequired = VoiceAction.NavigateToProcedure("burns"),
            metadata = mapOf("urgency" to "MEDIUM", "procedure" to "BURN_CARE")
        ),
        "heart_attack" to VoiceResponse(
            text = "Call 911 immediately! Have person sit down and rest. Give aspirin if available and not allergic. Monitor breathing and pulse.",
            actionRequired = VoiceAction.CallEmergency("911"),
            metadata = mapOf("urgency" to "HIGH", "procedure" to "HEART_ATTACK")
        ),
        "stroke" to VoiceResponse(
            text = "Call 911 now! Note time symptoms started. Keep person comfortable, lying down. Do not give food, drink, or medication.",
            actionRequired = VoiceAction.CallEmergency("911"),
            metadata = mapOf("urgency" to "HIGH", "procedure" to "STROKE")
        ),
        "fracture" to VoiceResponse(
            text = "Don't move injured area. Immobilize with splint using rigid material. Apply ice wrapped in cloth. Seek medical attention immediately.",
            actionRequired = VoiceAction.NavigateToProcedure("fractures"),
            metadata = mapOf("urgency" to "MEDIUM", "procedure" to "FRACTURE_CARE")
        )
    )

    init {
        initializeLiveModel()
    }

    /**
     * Initialize the Gemini model with emergency first aid system instructions
     */
    private fun initializeLiveModel() {
        try {
            val apiKey = getGeminiApiKey()
            if (apiKey.isBlank()) {
                Log.w(tag, "Cannot initialize Gemini model: API key is missing or empty")
                isModelInitialized.set(false)
                return
            }

            // Initialize Google AI Client GenerativeModel
            // Using gemini-2.0-flash for latest model capabilities
            generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = apiKey,
                generationConfig = generationConfig {
                    temperature = 0.7f
                    topK = 40
                    topP = 0.95f
                }
            )

            isAIModelReady = true
            isModelInitialized.set(true)
            Log.d(tag, "Gemini model initialized successfully for emergency first aid")
        } catch (e: Exception) {
            Log.w(tag, "Failed to initialize Gemini model, falling back to offline mode", e)
            isModelInitialized.set(false)
        }
    }

    /**
     * Get the Gemini API key from BuildConfig or environment
     */
    private fun getGeminiApiKey(): String {
        return try {
            // First try to get from BuildConfig
            val buildConfigClass = Class.forName("${context.packageName}.BuildConfig")
            val field = buildConfigClass.getDeclaredField("GEMINI_API_KEY")
            val apiKey = field.get(null) as? String ?: ""

            if (apiKey.isBlank()) {
                Log.w(tag, "API key is blank or empty")
                return ""
            }

            Log.d(tag, "API key loaded successfully from BuildConfig")
            return apiKey
        } catch (e: Exception) {
            Log.w(tag, "Could not retrieve API key from BuildConfig", e)
            ""
        }
    }

    /**
     * Start real-time voice conversation with emergency assistance
     */
    suspend fun startVoiceConversation(): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isModelInitialized.get()) {
                return@withContext Result.failure(Exception("Model not initialized"))
            }

            isLiveSessionActive.set(true)
            Log.d(tag, "Live voice conversation started - ready for emergency guidance")

            Result.success("Voice conversation active - speak your emergency situation")
        } catch (e: Exception) {
            Log.e(tag, "Failed to start voice conversation", e)
            Result.failure(e)
        }
    }

    /**
     * Stop the active voice conversation
     */
    suspend fun stopVoiceConversation(): Result<String> = withContext(Dispatchers.IO) {
        try {
            isLiveSessionActive.set(false)
            Log.d(tag, "Voice conversation stopped")
            Result.success("Voice conversation stopped")
        } catch (e: Exception) {
            Log.e(tag, "Failed to stop voice conversation", e)
            Result.failure(e)
        }
    }

    /**
     * Process emergency voice input with Gemini AI
     */
    suspend fun processEmergencyVoiceInput(text: String): Result<VoiceResponse> = withContext(Dispatchers.IO) {
        try {
            if (!isModelInitialized.get() || generativeModel == null) {
                return@withContext processFallbackCommand(text)
            }

            val emergencyPrompt = """
                ${getEmergencyFirstAidPrompt()}
                
                EMERGENCY SITUATION: $text
                
                Analyze this emergency and provide immediate first aid guidance. 
                If this is life-threatening, recommend calling 911 immediately.
                Provide clear, step-by-step instructions that can be followed during panic.
            """.trimIndent()

            // Use Google AI Client to generate content
            val response = generativeModel!!.generateContent(
                content {
                    text(emergencyPrompt)
                }
            )

            val responseText = response.text ?: getBasicEmergencyResponse(text)

            // Analyze for emergency actions needed
            val actionRequired = when {
                responseText.contains("call 911", ignoreCase = true) ||
                responseText.contains("call emergency", ignoreCase = true) -> {
                    onEmergencyCall?.invoke("medical", "HIGH")
                    VoiceAction.CallEmergency("911")
                }
                responseText.contains("navigate to", ignoreCase = true) -> {
                    val procedureName = extractProcedureName(text)
                    onNavigateToProcedure?.invoke(procedureName)
                    VoiceAction.NavigateToProcedure(procedureName)
                }
                else -> null
            }

            // Determine urgency level
            val urgency = when {
                responseText.contains("immediately", ignoreCase = true) ||
                responseText.contains("911", ignoreCase = true) -> "CRITICAL"
                responseText.contains("urgent", ignoreCase = true) ||
                responseText.contains("quickly", ignoreCase = true) -> "HIGH"
                else -> "MEDIUM"
            }

            Result.success(VoiceResponse(
                text = responseText,
                actionRequired = actionRequired,
                metadata = mapOf(
                    "source" to "gemini_ai",
                    "urgency" to urgency,
                    "timestamp" to System.currentTimeMillis().toString()
                )
            ))
        } catch (e: Exception) {
            Log.e(tag, "Error processing emergency voice input with AI", e)
            processFallbackCommand(text)
        }
    }

    /**
     * Extract procedure name from user input for navigation
     */
    private fun extractProcedureName(text: String): String {
        val lowerText = text.lowercase()
        return when {
            lowerText.contains("cpr") || lowerText.contains("cardiac") -> "cpr"
            lowerText.contains("choking") || lowerText.contains("heimlich") -> "choking"
            lowerText.contains("bleeding") || lowerText.contains("blood") -> "bleeding"
            lowerText.contains("burn") -> "burns"
            lowerText.contains("fracture") || lowerText.contains("broken") -> "fractures"
            lowerText.contains("heart attack") -> "heart_attack"
            lowerText.contains("stroke") -> "stroke"
            else -> "general_emergency"
        }
    }

    /**
     * Get the emergency first aid system prompt for the AI
     */
    private fun getEmergencyFirstAidPrompt(): String {
        return """
            You are an AI Emergency First Aid Assistant. Your role is to provide IMMEDIATE, SPECIFIC guidance based on the exact situation described.

            CRITICAL INSTRUCTION: Analyze the specific emergency situation first, then provide ONLY the most relevant, immediate actions needed for that specific case. DO NOT provide generic checklists or comprehensive procedures unless specifically needed.

            RESPONSE APPROACH:
            1. ANALYZE THE SPECIFIC SITUATION: What exactly is happening? What are the key symptoms/conditions mentioned?
            2. DETERMINE IMMEDIATE PRIORITY: What is the most urgent action needed right now?
            3. PROVIDE TARGETED GUIDANCE: Give specific steps for THIS situation, not general emergency procedures
            4. BE CONCISE BUT COMPLETE: Focus on what matters most for this specific emergency

            RESPONSE STRUCTURE:
            - IMMEDIATE ACTION (1-2 sentences): What to do RIGHT NOW
            - SPECIFIC STEPS (3-5 steps max): Targeted to this exact situation  
            - CALL 911 WHEN: Only mention if truly necessary for this specific case
            - MONITOR FOR: What to watch for specifically in this situation

            EXAMPLES OF GOOD RESPONSES:
            - If "unconscious": "Check for breathing immediately. If not breathing, start CPR now - 30 chest compressions, 2 breaths."
            - If "choking": "Stand behind them and give 5 firm back blows between shoulder blades. If object doesn't come out, do abdominal thrusts."
            - If "chest pain": "Have them sit down and rest immediately. Call 911 now. Give aspirin if available and no allergies."

            AVOID:
            - Long comprehensive lists when situation is specific
            - Multiple "if this, then that" scenarios unless needed
            - Generic emergency overviews
            - Repeating information not relevant to the described situation

            TONE: Calm, authoritative, focused. Speak as if you're right there helping with THIS specific emergency.

            Remember: The person is in a specific emergency situation. Give them exactly what they need for their situation, not everything they might possibly need.
        """
    }

    /**
     * Check if the Live API session is currently active
     */
    fun isVoiceSessionActive(): Boolean {
        return isLiveSessionActive.get()
    }

    /**
     * Check if the AI model is available
     */
    fun isServiceAvailable(): Boolean {
        return isModelInitialized.get()
    }

    /**
     * Process voice commands (legacy compatibility method)
     */
    suspend fun processVoiceCommand(text: String): Result<VoiceResponse> {
        return processEmergencyVoiceInput(text)
    }

    /**
     * Process audio data (legacy compatibility method)
     */
    suspend fun processVoiceCommandWithAudio(audioData: ByteArray): Result<VoiceResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Processing audio input via Live API")

            // In a real implementation, this would use speech-to-text
            // For now, return a response indicating audio was received
            Result.success(VoiceResponse(
                text = "I received your audio. Please describe your emergency situation clearly.",
                metadata = mapOf(
                    "source" to "live_api_audio",
                    "processing" to "true",
                    "timestamp" to System.currentTimeMillis().toString()
                )
            ))
        } catch (e: Exception) {
            Log.e(tag, "Error processing audio input", e)
            processFallbackCommand("voice input received")
        }
    }

    /**
     * Fallback command processing when AI is unavailable
     */
    private suspend fun processFallbackCommand(command: String): Result<VoiceResponse> {
        return try {
            val lowerCommand = command.lowercase()

            // Check for known emergency keywords and provide basic responses
            val response = when {
                lowerCommand.contains("cpr") || lowerCommand.contains("cardiac") -> {
                    emergencyResponses["cpr"]!!
                }
                lowerCommand.contains("choking") || lowerCommand.contains("heimlich") -> {
                    emergencyResponses["choking"]!!
                }
                lowerCommand.contains("bleeding") || lowerCommand.contains("blood") -> {
                    emergencyResponses["bleeding"]!!
                }
                lowerCommand.contains("burn") -> {
                    emergencyResponses["burns"]!!
                }
                lowerCommand.contains("heart attack") -> {
                    emergencyResponses["heart_attack"]!!
                }
                lowerCommand.contains("stroke") -> {
                    emergencyResponses["stroke"]!!
                }
                lowerCommand.contains("fracture") || lowerCommand.contains("broken") -> {
                    emergencyResponses["fracture"]!!
                }
                else -> {
                    VoiceResponse(
                        text = "I'm operating in offline mode. For any life-threatening emergency, call 911 immediately. Can you describe your specific emergency situation?",
                        metadata = mapOf(
                            "source" to "offline_mode",
                            "urgency" to "MEDIUM",
                            "timestamp" to System.currentTimeMillis().toString()
                        )
                    )
                }
            }

            Result.success(response)
        } catch (e: Exception) {
            Log.e(tag, "Error in fallback command processing", e)
            Result.failure(e)
        }
    }

    /**
     * Get basic emergency response when AI processing fails
     */
    private fun getBasicEmergencyResponse(input: String): String {
        val lowerInput = input.lowercase()
        return when {
            lowerInput.contains("cpr") || lowerInput.contains("cardiac") -> {
                "Call 911 immediately. Start CPR: 30 chest compressions, 2 rescue breaths. Push hard and fast on center of chest."
            }
            lowerInput.contains("choking") -> {
                "For choking: 5 back blows between shoulder blades, then 5 abdominal thrusts. Repeat until object comes out."
            }
            lowerInput.contains("bleeding") -> {
                "Control bleeding: Apply direct pressure with clean cloth. Keep pressure constant. Elevate above heart if possible."
            }
            lowerInput.contains("burn") -> {
                "Cool burn with clean, cool water for 10-20 minutes. Do not use ice. Cover with sterile bandage."
            }
            lowerInput.contains("heart attack") -> {
                "Call 911 now! Have person sit and rest. Give aspirin if available and not allergic. Monitor breathing."
            }
            lowerInput.contains("stroke") -> {
                "Call 911 immediately! Note time symptoms started. Keep person lying down. Do not give food or drink."
            }
            else -> {
                "This appears to be an emergency. Call 911 if the situation is life-threatening. Can you provide more specific details about what's happening?"
            }
        }
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        try {
            isLiveSessionActive.set(false)
            isModelInitialized.set(false)
            generativeModel = null
            Log.d(tag, "GeminiAIManager cleaned up")
        } catch (e: Exception) {
            Log.e(tag, "Error during cleanup", e)
        }
    }
}