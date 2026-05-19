package com.ironcore.metrics.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Long = 0
)

@Entity(tableName = "workout_sets")
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val exerciseId: Long,
    val reps: Int,
    val weight: Float,
    val rpe: Int? = null, // Rate of Perceived Exertion (1-10)
    val restTimeSeconds: Int? = null,
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
