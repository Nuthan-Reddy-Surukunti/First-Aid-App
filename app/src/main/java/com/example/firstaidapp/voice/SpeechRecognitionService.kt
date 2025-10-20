package com.example.firstaidapp.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Manages Speech Recognition functionality for voice assistant
 */
class SpeechRecognitionService(private val context: Context) {

    private val tag = "SpeechRecognitionService"
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    /**
     * Initialize speech recognizer
     */
    fun initialize() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(tag, "Speech recognition not available on this device")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        Log.d(tag, "Speech recognizer initialized")
    }

    /**
     * Start listening with callback approach
     */
    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onReadyForSpeech: () -> Unit = {},
        onPartialResult: (String) -> Unit = {}
    ) {
        if (speechRecognizer == null) {
            initialize()
        }

        if (isListening) {
            Log.w(tag, "Already listening")
            return
        }

        val intent = createRecognitionIntent()

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                Log.d(tag, "Ready for speech")
                onReadyForSpeech()
            }

            override fun onBeginningOfSpeech() {
                Log.d(tag, "Beginning of speech")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Real-time audio level feedback
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Audio buffer received
            }

            override fun onEndOfSpeech() {
                isListening = false
                Log.d(tag, "End of speech")
            }

            override fun onError(error: Int) {
                isListening = false
                val errorMessage = getErrorMessage(error)
                Log.e(tag, "Recognition error: $errorMessage")
                onError(errorMessage)
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    Log.d(tag, "Recognition result: $recognizedText")
                    onResult(recognizedText)
                } else {
                    onError("No speech recognized")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                )
                if (!matches.isNullOrEmpty()) {
                    val partialText = matches[0]
                    Log.d(tag, "Partial result: $partialText")
                    onPartialResult(partialText)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Additional events
            }
        })

        speechRecognizer?.startListening(intent)
        Log.d(tag, "Started listening")
    }

    /**
     * Start listening with Flow for reactive approach
     */
    fun startListeningFlow(): Flow<RecognitionResult> = callbackFlow {
        if (speechRecognizer == null) {
            initialize()
        }

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            trySend(RecognitionResult.Error("Speech recognition not available"))
            close()
            return@callbackFlow
        }

        val intent = createRecognitionIntent()

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                trySend(RecognitionResult.ReadyForSpeech)
            }

            override fun onBeginningOfSpeech() {
                trySend(RecognitionResult.BeginningOfSpeech)
            }

            override fun onRmsChanged(rmsdB: Float) {
                trySend(RecognitionResult.RmsChanged(rmsdB))
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Buffer received
            }

            override fun onEndOfSpeech() {
                isListening = false
                trySend(RecognitionResult.EndOfSpeech)
            }

            override fun onError(error: Int) {
                isListening = false
                trySend(RecognitionResult.Error(getErrorMessage(error)))
                close()
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidences = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                if (!matches.isNullOrEmpty()) {
                    val confidence = confidences?.get(0) ?: 0f
                    trySend(RecognitionResult.FinalResult(matches[0], confidence))
                } else {
                    trySend(RecognitionResult.Error("No speech recognized"))
                }
                close()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                )
                if (!matches.isNullOrEmpty()) {
                    trySend(RecognitionResult.PartialResult(matches[0]))
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Additional events
            }
        })

        speechRecognizer?.startListening(intent)

        awaitClose {
            stopListening()
        }
    }

    /**
     * Create recognition intent with optimal settings for emergency situations
     */
    private fun createRecognitionIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1500L)
        }
    }

    /**
     * Stop listening
     */
    fun stopListening() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
            Log.d(tag, "Stopped listening")
        }
    }

    /**
     * Cancel recognition
     */
    fun cancel() {
        speechRecognizer?.cancel()
        isListening = false
        Log.d(tag, "Recognition cancelled")
    }

    /**
     * Check if currently listening
     */
    fun isCurrentlyListening(): Boolean = isListening

    /**
     * Release resources
     */
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
        Log.d(tag, "Speech recognizer destroyed")
    }

    /**
     * Convert error code to human-readable message
     */
    private fun getErrorMessage(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error: $error"
        }
    }

    /**
     * Recognition result sealed class
     */
    sealed class RecognitionResult {
        object ReadyForSpeech : RecognitionResult()
        object BeginningOfSpeech : RecognitionResult()
        object EndOfSpeech : RecognitionResult()
        data class RmsChanged(val rmsdB: Float) : RecognitionResult()
        data class PartialResult(val text: String) : RecognitionResult()
        data class FinalResult(val text: String, val confidence: Float) : RecognitionResult()
        data class Error(val message: String) : RecognitionResult()
    }

    companion object {
        /**
         * Check if speech recognition is available
         */
        fun isAvailable(context: Context): Boolean {
            return SpeechRecognizer.isRecognitionAvailable(context)
        }
    }
}

