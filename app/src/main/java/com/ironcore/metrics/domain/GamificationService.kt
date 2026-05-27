package com.ironcore.metrics.domain

import com.ironcore.metrics.data.local.dao.WorkoutDao
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamificationService @Inject constructor(
    private val workoutDao: WorkoutDao
) {
    /**
     * Calculates the current workout streak in days.
     */
    suspend fun calculateCurrentStreak(): Int {
        val today = LocalDate.now()
        var streak = 0
        var checkDate = today
        
        while (true) {
            val startOfDay = checkDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = checkDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            val workouts = workoutDao.getWorkoutsSince(startOfDay)
            val hasWorkout = workouts.any { it.timestamp < endOfDay }
            
            if (hasWorkout) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                // Allow one day gap if today has no workout yet
                if (checkDate == today) {
                    checkDate = checkDate.minusDays(1)
                    continue
                }
                break
            }
        }
        return streak
    }

    /**
     * Returns a list of badges earned by the user.
     */
    suspend fun getEarnedBadges(): List<Badge> {
        val streak = calculateCurrentStreak()
        val allWorkouts = workoutDao.getWorkoutsSince(0) // All time
        
        return buildList {
            if (allWorkouts.isNotEmpty()) add(Badge("First Step", "Completed your first workout"))
            if (streak >= 3) add(Badge("Iron Consistent", "3-day workout streak"))
            if (streak >= 7) add(Badge("Weekly Warrior", "7-day workout streak"))
            if (allWorkouts.size >= 10) add(Badge("Veteran", "Completed 10 workouts"))
        }
    }
}

data class Badge(val name: String, val description: String)
