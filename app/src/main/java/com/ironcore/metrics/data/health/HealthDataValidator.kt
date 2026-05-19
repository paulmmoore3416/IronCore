package com.ironcore.metrics.data.health

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validates health data to ensure data quality and handle edge cases.
 */
@Singleton
class HealthDataValidator @Inject constructor() {
    
    companion object {
        private const val TAG = "HealthDataValidator"
        
        // Physiological limits for validation
        private const val MIN_HEART_RATE = 30L
        private const val MAX_HEART_RATE = 220L
        private const val MIN_WEIGHT_KG = 20.0
        private const val MAX_WEIGHT_KG = 300.0
        private const val MAX_DAILY_STEPS = 100000L
        private const val MAX_DAILY_CALORIES = 10000.0
        private const val MIN_HYDRATION_ML = 0
        private const val MAX_HYDRATION_ML = 10000
    }

    /**
     * Validates heart rate reading.
     * @return validated heart rate or 0 if invalid
     */
    fun validateHeartRate(heartRate: Long): Long {
        return when {
            heartRate == 0L -> {
                Log.d(TAG, "Heart rate is 0 - no data available")
                0L
            }
            heartRate < MIN_HEART_RATE -> {
                Log.w(TAG, "Heart rate $heartRate BPM is below minimum threshold $MIN_HEART_RATE BPM - rejecting")
                0L
            }
            heartRate > MAX_HEART_RATE -> {
                Log.w(TAG, "Heart rate $heartRate BPM exceeds maximum threshold $MAX_HEART_RATE BPM - rejecting")
                0L
            }
            else -> {
                Log.d(TAG, "Heart rate $heartRate BPM is valid")
                heartRate
            }
        }
    }

    /**
     * Validates weight reading.
     * @return validated weight or 0.0 if invalid
     */
    fun validateWeight(weight: Double): Double {
        return when {
            weight == 0.0 -> {
                Log.d(TAG, "Weight is 0 - no data available")
                0.0
            }
            weight < MIN_WEIGHT_KG -> {
                Log.w(TAG, "Weight $weight kg is below minimum threshold $MIN_WEIGHT_KG kg - rejecting")
                0.0
            }
            weight > MAX_WEIGHT_KG -> {
                Log.w(TAG, "Weight $weight kg exceeds maximum threshold $MAX_WEIGHT_KG kg - rejecting")
                0.0
            }
            else -> {
                Log.d(TAG, "Weight $weight kg is valid")
                weight
            }
        }
    }

    /**
     * Validates step count.
     * @return validated steps or 0 if invalid
     */
    fun validateSteps(steps: Long): Long {
        return when {
            steps < 0 -> {
                Log.w(TAG, "Steps $steps is negative - rejecting")
                0L
            }
            steps > MAX_DAILY_STEPS -> {
                Log.w(TAG, "Steps $steps exceeds maximum daily threshold $MAX_DAILY_STEPS - capping")
                MAX_DAILY_STEPS
            }
            else -> {
                Log.d(TAG, "Steps $steps is valid")
                steps
            }
        }
    }

    /**
     * Validates calorie data.
     * @return validated calories or 0.0 if invalid
     */
    fun validateCalories(calories: Double): Double {
        return when {
            calories < 0 -> {
                Log.w(TAG, "Calories $calories is negative - rejecting")
                0.0
            }
            calories > MAX_DAILY_CALORIES -> {
                Log.w(TAG, "Calories $calories exceeds maximum daily threshold $MAX_DAILY_CALORIES - capping")
                MAX_DAILY_CALORIES
            }
            else -> {
                Log.d(TAG, "Calories $calories is valid")
                calories
            }
        }
    }

    /**
     * Validates hydration amount.
     * @return validated hydration or 0 if invalid
     */
    fun validateHydration(hydrationMl: Int): Int {
        return when {
            hydrationMl < MIN_HYDRATION_ML -> {
                Log.w(TAG, "Hydration $hydrationMl ml is negative - rejecting")
                0
            }
            hydrationMl > MAX_HYDRATION_ML -> {
                Log.w(TAG, "Hydration $hydrationMl ml exceeds maximum threshold $MAX_HYDRATION_ML ml - capping")
                MAX_HYDRATION_ML
            }
            else -> {
                Log.d(TAG, "Hydration $hydrationMl ml is valid")
                hydrationMl
            }
        }
    }

    /**
     * Validates recovery score.
     * @return validated score between 0-100
     */
    fun validateRecoveryScore(score: Int): Int {
        return score.coerceIn(0, 100).also {
            if (it != score) {
                Log.w(TAG, "Recovery score $score was out of range, clamped to $it")
            }
        }
    }
}
