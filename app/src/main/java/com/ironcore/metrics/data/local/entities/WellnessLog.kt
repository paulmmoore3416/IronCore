package com.ironcore.metrics.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wellness_logs")
data class WellnessLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String,
    val energyLevel: Int, // 1-10
    val notes: String? = null
)
