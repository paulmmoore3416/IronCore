package com.ironcore.metrics.ui.workout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

/**
 * Helper class for voice command workout logging
 * Handles speech recognition and text-to-speech feedback
 */
class VoiceCommandHelper(
    private val context: Context,
    private val onCommandParsed: (VoiceCommand) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTextToSpeechReady = false

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _lastRecognizedText = MutableStateFlow("")
    val lastRecognizedText: StateFlow<String> = _lastRecognizedText

    init {
        initializeSpeechRecognizer()
        initializeTextToSpeech()
    }

    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        Log.d(TAG, "Ready for speech")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d(TAG, "Speech started")
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        Log.d(TAG, "Speech ended")
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                            else -> "Unknown error"
                        }
                        Log.e(TAG, "Speech recognition error: $errorMessage")
                        speak("Sorry, I didn't catch that. Please try again.")
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val recognizedText = matches[0]
                            _lastRecognizedText.value = recognizedText
                            Log.d(TAG, "Recognized: $recognizedText")
                            parseCommand(recognizedText)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        } else {
            Log.e(TAG, "Speech recognition not available on this device")
        }
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                isTextToSpeechReady = result != TextToSpeech.LANG_MISSING_DATA && 
                                     result != TextToSpeech.LANG_NOT_SUPPORTED
                if (isTextToSpeechReady) {
                    Log.d(TAG, "TextToSpeech initialized successfully")
                } else {
                    Log.e(TAG, "TextToSpeech language not supported")
                }
            } else {
                Log.e(TAG, "TextToSpeech initialization failed")
            }
        }
    }

    fun startListening() {
        if (_isListening.value) {
            Log.w(TAG, "Already listening")
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizer?.startListening(intent)
            speak("Listening")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition", e)
            _isListening.value = false
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    private fun parseCommand(text: String) {
        val lowerText = text.lowercase()
        
        // Pattern: "log X reps at Y pounds/kg RPE Z"
        // Examples:
        // - "log 10 reps at 225 pounds RPE 8"
        // - "log 12 reps at 100 kilograms RPE 7"
        // - "log 8 reps 315 pounds"
        
        val repsPattern = Regex("""(\d+)\s*(?:reps?|repetitions?)""")
        val weightPattern = Regex("""(\d+(?:\.\d+)?)\s*(?:pounds?|lbs?|kilograms?|kgs?|kg)""")
        val rpePattern = Regex("""rpe\s*(\d+)""")
        
        val repsMatch = repsPattern.find(lowerText)
        val weightMatch = weightPattern.find(lowerText)
        val rpeMatch = rpePattern.find(lowerText)
        
        val reps = repsMatch?.groupValues?.get(1)?.toIntOrNull()
        val weightStr = weightMatch?.groupValues?.get(1)?.toFloatOrNull()
        val rpe = rpeMatch?.groupValues?.get(1)?.toIntOrNull()
        
        // Convert pounds to kg if needed
        val weight = if (weightStr != null && lowerText.contains(Regex("""pounds?|lbs?"""))) {
            weightStr * 0.453592f // Convert lbs to kg
        } else {
            weightStr
        }
        
        if (reps != null && weight != null) {
            val command = VoiceCommand(
                reps = reps,
                weight = weight,
                rpe = rpe,
                rawText = text
            )
            
            val confirmation = buildString {
                append("Logged $reps reps at ${weight.toInt()} kilograms")
                if (rpe != null) {
                    append(" RPE $rpe")
                }
            }
            
            speak(confirmation)
            onCommandParsed(command)
        } else {
            speak("I couldn't understand that. Please say something like: log 10 reps at 225 pounds RPE 8")
        }
    }

    fun speak(text: String) {
        if (isTextToSpeechReady) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun cleanup() {
        speechRecognizer?.destroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        speechRecognizer = null
        textToSpeech = null
    }

    companion object {
        private const val TAG = "VoiceCommandHelper"
    }
}

/**
 * Parsed voice command data
 */
data class VoiceCommand(
    val reps: Int,
    val weight: Float,
    val rpe: Int? = null,
    val rawText: String
)