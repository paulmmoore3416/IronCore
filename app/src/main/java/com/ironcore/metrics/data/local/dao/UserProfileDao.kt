package com.ironcore.metrics.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ironcore.metrics.data.local.entities.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 0 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 0 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET age = :age WHERE id = 0")
    suspend fun updateAge(age: Int)

    @Query("UPDATE user_profile SET weightKg = :weight WHERE id = 0")
    suspend fun updateWeight(weight: Float)

    @Query("UPDATE user_profile SET dailyCalorieTarget = :target WHERE id = 0")
    suspend fun updateCalorieTarget(target: Int)
}
