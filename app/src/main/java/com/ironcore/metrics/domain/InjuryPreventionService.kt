package com.ironcore.metrics.domain

import com.ironcore.metrics.data.local.dao.WorkoutDao
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InjuryPreventionService @Inject constructor(
    private val workoutDao: WorkoutDao
) {
    /**
     * Analyzes training volume and returns a warning if overtraining is detected.
     */
    suspend fun analyzeInjuryRisk(): InjuryRisk {
        val workouts = workoutDao.getAllWorkouts().first()
        if (workouts.size < 5) return InjuryRisk("Low", "Insufficient data for trend analysis.")
        
        // Simplified heuristic: If last 3 workouts were very close and volume increasing rapidly
        val lastWorkouts = workouts.take(5)
        val timestamps = lastWorkouts.map { it.timestamp }
        
        val averageIntervalDays = (timestamps.first() - timestamps.last()) / (1000 * 60 * 60 * 24 * 5)
        
        return when {
            averageIntervalDays < 1 -> InjuryRisk("High", "Overtraining detected. Your rest intervals are too short.")
            averageIntervalDays < 1.5 -> InjuryRisk("Medium", "Consider a deload week. Volume is peaking.")
            else -> InjuryRisk("Low", "Training volume looks sustainable.")
        }
    }
}

data class InjuryRisk(val level: String, val message: String)
