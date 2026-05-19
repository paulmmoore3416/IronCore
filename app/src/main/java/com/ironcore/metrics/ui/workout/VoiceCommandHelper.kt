package com.ironcore.metrics.ui.workout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Enhancement 1: Voice Command Integration
 * Enables hands-free workout logging via voice commands
 * Commands: "log 10 reps", "add 50 pounds", "complete set"
 */
class VoiceCommandHelper(private val context: Context) {
    
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()
    
    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var onCommandRecognized: ((VoiceCommand) -> Unit)? = null
    
    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }
    }
    
    fun startListening(onCommand: (VoiceCommand) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            return
        }
        
        onCommandRecognized = onCommand
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
            }
            
            override fun onBeginningOfSpeech() {}
            
            override fun onRmsChanged(rmsdB: Float) {}
            
            override fun onBufferReceived(buffer: ByteArray?) {}
            
            override fun onEndOfSpeech() {
                _isListening.value = false
            }
            
            override fun onError(error: Int) {
                _isListening.value = false
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    _recognizedText.value = text
                    parseCommand(text)?.let { command ->
                        onCommandRecognized?.invoke(command)
                    }
                }
                _isListening.value = false
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _recognizedText.value = matches[0]
                }
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        
        speechRecognizer?.startListening(intent)
    }
    
    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }
    
    private fun parseCommand(text: String): VoiceCommand? {
        val lowerText = text.lowercase()
        
        // Parse "log X reps" or "X reps"
        val repsPattern = Regex("""(\d+)\s*reps?""")
        repsPattern.find(lowerText)?.let { match ->
            val reps = match.groupValues[1].toIntOrNull()
            if (reps != null) {
                return VoiceCommand.LogReps(reps)
            }
        }
        
        // Parse "add X pounds/kilos" or "X pounds/kilos"
        val weightPattern = Regex("""(\d+)\s*(pounds?|kilos?|kg|lbs?)""")
        weightPattern.find(lowerText)?.let { match ->
            val weight = match.groupValues[1].toDoubleOrNull()
            val unit = match.groupValues[2]
            if (weight != null) {
                val weightInKg = if (unit.startsWith("p") || unit.startsWith("lb")) {
                    weight * 0.453592 // Convert pounds to kg
                } else {
                    weight
                }
                return VoiceCommand.SetWeight(weightInKg)
            }
        }
        
        // Parse "complete set" or "finish set"
        if (lowerText.contains("complete") || lowerText.contains("finish")) {
            if (lowerText.contains("set")) {
                return VoiceCommand.CompleteSet
            }
        }
        
        // Parse "start workout" or "begin workout"
        if ((lowerText.contains("start") || lowerText.contains("begin")) && lowerText.contains("workout")) {
            return VoiceCommand.StartWorkout
        }
        
        // Parse "end workout" or "finish workout"
        if ((lowerText.contains("end") || lowerText.contains("finish")) && lowerText.contains("workout")) {
            return VoiceCommand.EndWorkout
        }
        
        return null
    }
    
    fun cleanup() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

sealed class VoiceCommand {
    data class LogReps(val reps: Int) : VoiceCommand()
    data class SetWeight(val weightKg: Double) : VoiceCommand()
    object CompleteSet : VoiceCommand()
    object StartWorkout : VoiceCommand()
    object EndWorkout : VoiceCommand()
}
