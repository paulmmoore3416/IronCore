package com.ironcore.metrics.domain

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service that provides AI capabilities using on-device AICore (Simulated).
 * Faster and private but potentially less comprehensive than remote models.
 */
@Singleton
class OnDeviceAiService @Inject constructor() {

    fun getBasicRecoveryAdvice(score: Int): String {
        return when {
            score > 80 -> "Your vitals are optimal. Push for a PR today!"
            score > 50 -> "Good readiness. Stick to your planned volume."
            else -> "High fatigue detected. Opt for active recovery."
        }
    }

    fun summarizeMetrics(steps: Long, hr: Long): String {
        return "Activity: $steps steps. HR: $hr BPM. Status: Normal."
    }
}
