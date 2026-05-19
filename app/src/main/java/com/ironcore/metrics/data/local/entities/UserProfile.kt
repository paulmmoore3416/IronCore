package com.ironcore.metrics.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 0, // Single user app
    val name: String,
    val age: Int,
    val weightKg: Float,
    val heightCm: Float,
    val fitnessGoal: String,
    val dailyCalorieTarget: Int
)
