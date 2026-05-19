package com.ironcore.metrics.data.health

import android.util.Log
import androidx.health.connect.client.records.ExerciseSessionRecord
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculates dynamic heart rate thresholds based on user age and activity state.
 * 
 * Uses standard formulas:
 * - Max HR = 220 - age
 * - Critical threshold varies by activity type:
 *   - Sedentary/Unknown: 85% of max HR
 *   - Light activity: 90% of max HR
 *   - Moderate activity: 95% of max HR
 *   - Vigorous activity: 98% of max HR
 */
@Singleton
class HeartRateThresholdCalculator @Inject constructor() {

    companion object {
        private const val TAG = "HRThresholdCalc"
        private const val DEFAULT_AGE = 30
        private const val DEFAULT_MAX_HR = 190 // 220 - 30
    }

    /**
     * Calculates the maximum heart rate based on age.
     * Uses the standard formula: 220 - age
     */
    fun calculateMaxHeartRate(age: Int?): Int {
        val validAge = age?.takeIf { it in 10..100 } ?: DEFAULT_AGE
        return 220 - validAge
    }

    /**
     * Calculates the critical heart rate threshold based on age and activity type.
     * 
     * @param age User's age (null defaults to 30)
     * @param activityType The type of activity from Health Connect
     * @return The critical HR threshold in BPM
     */
    fun calculateCriticalThreshold(
        age: Int?,
        activityType: Int?
    ): Int {
        val maxHR = calculateMaxHeartRate(age)
        val percentage = getThresholdPercentage(activityType)
        
        val threshold = (maxHR * percentage).toInt()
        
        Log.d(TAG, "Calculated critical HR threshold: $threshold BPM " +
                "(age: ${age ?: DEFAULT_AGE}, maxHR: $maxHR, " +
                "activity: ${getActivityName(activityType)}, percentage: ${percentage * 100}%)")
        
        return threshold
    }

    /**
     * Determines the threshold percentage based on activity type.
     */
    private fun getThresholdPercentage(activityType: Int?): Double {
        return when (activityType) {
            // Vigorous activities (running, cycling, HIIT, etc.)
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL,
            ExerciseSessionRecord.EXERCISE_TYPE_CYCLING,
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY,
            ExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE,
            ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING,
            ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING_MACHINE,
            ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING -> 0.98

            // Moderate activities (walking, swimming, etc.)
            ExerciseSessionRecord.EXERCISE_TYPE_WALKING,
            ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER,
            ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL,
            ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL,
            ExerciseSessionRecord.EXERCISE_TYPE_DANCING,
            ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> 0.95

            // Light activities (yoga, stretching, etc.)
            ExerciseSessionRecord.EXERCISE_TYPE_YOGA,
            ExerciseSessionRecord.EXERCISE_TYPE_PILATES,
            ExerciseSessionRecord.EXERCISE_TYPE_STRETCHING,
            ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING,
            ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING -> 0.90

            // Sedentary or unknown - most conservative threshold
            else -> 0.85
        }
    }

    /**
     * Gets a human-readable activity name for logging.
     */
    private fun getActivityName(activityType: Int?): String {
        return when (activityType) {
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "Running"
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL -> "Treadmill Running"
            ExerciseSessionRecord.EXERCISE_TYPE_CYCLING -> "Cycling"
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> "Biking"
            ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "Walking"
            ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> "Swimming (Pool)"
            ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> "Yoga"
            ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> "Strength Training"
            ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING -> "HIIT"
            null -> "Unknown/Sedentary"
            else -> "Other (Type: $activityType)"
        }
    }

    /**
     * Checks if the given heart rate exceeds the critical threshold.
     */
    fun isHeartRateCritical(
        heartRate: Long,
        age: Int?,
        activityType: Int?
    ): Boolean {
        val threshold = calculateCriticalThreshold(age, activityType)
        val isCritical = heartRate > threshold
        
        if (isCritical) {
            Log.w(TAG, "CRITICAL: HR $heartRate BPM exceeds threshold $threshold BPM " +
                    "(age: ${age ?: DEFAULT_AGE}, activity: ${getActivityName(activityType)})")
        }
        
        return isCritical
    }
}
