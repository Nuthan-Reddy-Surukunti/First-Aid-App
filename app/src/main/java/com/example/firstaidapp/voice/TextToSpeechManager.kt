package com.example.firstaidapp.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.firstaidapp.data.voice.VoicePreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

/**
 * Manages Text-to-Speech functionality for voice assistant
 */
class TextToSpeechManager(private val context: Context) {

    private val tag = "TextToSpeechManager"
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var currentPreferences: VoicePreferences = VoicePreferences()

    /**
     * Initialize TTS engine
     */
    fun initialize(onInitialized: (Boolean) -> Unit) {
        try {
            // Clear any existing TTS instance
            tts?.shutdown()
            tts = null

            tts = TextToSpeech(context) { status ->
                when (status) {
                    TextToSpeech.SUCCESS -> {
                        tts?.let { engine ->
                            val langResult = engine.setLanguage(Locale.US)
                            when (langResult) {
                                TextToSpeech.LANG_MISSING_DATA -> {
                                    Log.w(tag, "Language data missing, trying system default")
                                    engine.setLanguage(Locale.getDefault())
                                }
                                TextToSpeech.LANG_NOT_SUPPORTED -> {
                                    Log.w(tag, "Language not supported, using system default")
                                    engine.setLanguage(Locale.getDefault())
                                }
                                else -> {
                                    Log.d(tag, "Language set successfully")
                                }
                            }

                            try {
                                engine.setSpeechRate(currentPreferences.voiceSpeed)
                                engine.setPitch(currentPreferences.voicePitch)
                            } catch (e: Exception) {
                                Log.w(tag, "Failed to set TTS parameters, using defaults: ${e.message}")
                                // Use safe defaults
                                try {
                                    engine.setSpeechRate(1.0f)
                                    engine.setPitch(1.0f)
                                } catch (e2: Exception) {
                                    Log.w(tag, "Failed to set default TTS parameters: ${e2.message}")
                                }
                            }
                        }

                        isInitialized = true
                        Log.d(tag, "TTS initialized successfully")
                        onInitialized(true)
                    }
                    TextToSpeech.ERROR -> {
                        Log.e(tag, "TTS initialization failed with ERROR - attempting retry")
                        isInitialized = false
                        // Retry once after a delay
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            retryInitialization(onInitialized)
                        }, 1000)
                    }
                    else -> {
                        Log.e(tag, "TTS initialization failed with unknown status: $status")
                        isInitialized = false
                        onInitialized(false)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception during TTS initialization: ${e.message}", e)
            isInitialized = false
            onInitialized(false)
        }
    }

    /**
     * Retry TTS initialization once
     */
    private fun retryInitialization(onInitialized: (Boolean) -> Unit) {
        try {
            Log.d(tag, "Retrying TTS initialization...")
            tts?.shutdown()
            tts = null

            tts = TextToSpeech(context) { status ->
                isInitialized = status == TextToSpeech.SUCCESS
                if (isInitialized) {
                    Log.d(tag, "TTS retry successful")
                } else {
                    Log.e(tag, "TTS retry failed")
                }
                onInitialized(isInitialized)
            }
        } catch (e: Exception) {
            Log.e(tag, "Retry initialization failed: ${e.message}")
            onInitialized(false)
        }
    }

    /**
     * Speak text with callbacks
     */
    fun speak(
        text: String,
        queueMode: Int = TextToSpeech.QUEUE_FLUSH,
        utteranceId: String = "emergency_guidance",
        onStart: (() -> Unit)? = null,
        onDone: (() -> Unit)? = null,
        onError: (() -> Unit)? = null
    ) {
        if (!isInitialized || tts == null) {
            Log.w(tag, "TTS not initialized")
            onError?.invoke()
            return
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d(tag, "Speech started: $utteranceId")
                onStart?.invoke()
            }

            override fun onDone(utteranceId: String?) {
                Log.d(tag, "Speech completed: $utteranceId")
                onDone?.invoke()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                Log.e(tag, "Speech error: $utteranceId")
                onError?.invoke()
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e(tag, "Speech error: $utteranceId, code: $errorCode")
                onError?.invoke()
            }
        })

        val result = tts?.speak(text, queueMode, null, utteranceId)
        if (result == TextToSpeech.ERROR) {
            Log.e(tag, "Error speaking text: $text")
            onError?.invoke()
        }
    }

    /**
     * Speak text with Flow for reactive approach
     */
    fun speakWithFlow(text: String): Flow<SpeechState> = callbackFlow {
        if (!isInitialized || tts == null) {
            trySend(SpeechState.Error("TTS not initialized"))
            close()
            return@callbackFlow
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                trySend(SpeechState.Speaking)
            }

            override fun onDone(utteranceId: String?) {
                trySend(SpeechState.Completed)
                close()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                trySend(SpeechState.Error("Speech error"))
                close()
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                trySend(SpeechState.Error("Speech error: $errorCode"))
                close()
            }
        })

        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "flow_utterance")
        if (result == TextToSpeech.ERROR) {
            trySend(SpeechState.Error("Failed to start speech"))
            close()
        }

        awaitClose {
            // Cleanup if needed
        }
    }

    /**
     * Stop speaking
     */
    fun stop() {
        tts?.stop()
        Log.d(tag, "Speech stopped")
    }

    /**
     * Update voice preferences
     */
    fun updatePreferences(preferences: VoicePreferences) {
        currentPreferences = preferences
        tts?.apply {
            setSpeechRate(preferences.voiceSpeed)
            setPitch(preferences.voicePitch)
        }
        Log.d(tag, "Preferences updated: speed=${preferences.voiceSpeed}, pitch=${preferences.voicePitch}")
    }

    /**
     * Check if currently speaking
     */
    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }

    /**
     * Get available languages
     */
    fun getAvailableLanguages(): Set<Locale> {
        return tts?.availableLanguages ?: emptySet()
    }

    /**
     * Set language
     */
    fun setLanguage(locale: Locale): Boolean {
        val result = tts?.setLanguage(locale)
        return result == TextToSpeech.LANG_AVAILABLE ||
               result == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
               result == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
    }

    /**
     * Release resources
     */
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        Log.d(tag, "TTS shutdown")
    }

    /**
     * Speech state for reactive flow
     */
    sealed class SpeechState {
        object Speaking : SpeechState()
        object Completed : SpeechState()
        data class Error(val message: String) : SpeechState()
    }

    companion object {
        /**
         * Check if TTS is available on device
         */
        fun isAvailable(context: Context): Boolean {
            val tts = TextToSpeech(context) { }
            val available = tts.engines.isNotEmpty()
            tts.shutdown()
            return available
        }
    }
}

