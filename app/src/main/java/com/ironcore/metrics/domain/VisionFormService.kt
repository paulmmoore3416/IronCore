package com.ironcore.metrics.domain

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service that provides feedback on exercise form using computer vision (Simulated).
 */
@Singleton
class VisionFormService @Inject constructor() {

    /**
     * Analyzes a frame or data point and returns feedback.
     */
    fun analyzeForm(exercise: String, keypoints: List<Point3D>): FormFeedback {
        // Simulation logic
        return when (exercise.lowercase()) {
            "squat" -> {
                val depth = keypoints.firstOrNull()?.z ?: 0f
                if (depth > 0.8f) FormFeedback("Perfect Depth!", true)
                else FormFeedback("Go Lower", false)
            }
            else -> FormFeedback("Form looks stable", true)
        }
    }
}

data class Point3D(val x: Float, val y: Float, val z: Float)
data class FormFeedback(val message: String, val isGood: Boolean)
