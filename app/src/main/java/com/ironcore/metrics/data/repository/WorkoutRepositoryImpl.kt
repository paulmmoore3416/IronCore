package com.ironcore.metrics.data.repository

import com.ironcore.metrics.data.local.dao.WorkoutDao
import com.ironcore.metrics.data.local.entities.Workout
import com.ironcore.metrics.data.local.entities.WorkoutSet
import com.ironcore.metrics.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao
) : WorkoutRepository {
    override fun getAllWorkouts(): Flow<List<Workout>> = workoutDao.getAllWorkouts()

    override suspend fun saveWorkout(workout: Workout): Long = workoutDao.insertWorkout(workout)

    override suspend fun saveWorkoutSet(workoutSet: WorkoutSet) = workoutDao.insertWorkoutSet(workoutSet)

    override fun getSetsForWorkout(workoutId: Long): Flow<List<WorkoutSet>> = 
        workoutDao.getSetsForWorkout(workoutId)
}
