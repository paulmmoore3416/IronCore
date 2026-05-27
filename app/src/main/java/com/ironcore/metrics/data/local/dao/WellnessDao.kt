package com.ironcore.metrics.data.local.dao

import androidx.room.*
import com.ironcore.metrics.data.local.entities.WellnessLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WellnessDao {
    @Query("SELECT * FROM wellness_logs ORDER BY timestamp DESC")
    fun getAllWellnessLogs(): Flow<List<WellnessLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWellnessLog(log: WellnessLog)
}
