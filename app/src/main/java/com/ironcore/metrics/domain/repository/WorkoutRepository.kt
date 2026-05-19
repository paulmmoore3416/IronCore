package com.ironcore.metrics.domain.repository

import com.ironcore.metrics.data.local.entities.Workout
import com.ironcore.metrics.data.local.entities.WorkoutSet
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllWorkouts(): Flow<List<Workout>>
    suspend fun saveWorkout(workout: Workout): Long
    suspend fun saveWorkoutSet(workoutSet: WorkoutSet)
    fun getSetsForWorkout(workoutId: Long): Flow<List<WorkoutSet>>
}
