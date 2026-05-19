package com.ironcore.metrics.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hydration_logs",
    indices = [Index(value = ["timestamp"], name = "idx_hydration_timestamp")]
)
data class HydrationLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)
