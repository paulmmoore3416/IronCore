package com.ironcore.metrics.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the connection and data exchange with Google Health Connect.
 * 
 * This class handles permission checking, SDK availability, and provides 
 * access to the underlying HealthConnectClient.
 */
@Singleton
class HealthConnectManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getWritePermission(WeightRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    suspend fun hasAllPermissions(): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(permissions)
    }

    fun isAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    }

    suspend fun readStepsByTimeRange(startTime: Instant, endTime: Instant): Long {
        if (!hasAllPermissions()) return 0L
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records.sumOf { it.count }
    }

    suspend fun readActiveCalories(startTime: Instant, endTime: Instant): Double {
        if (!hasAllPermissions()) return 0.0
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = ActiveCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records.sumOf { it.energy.inKilocalories }
    }

    suspend fun readLatestHeartRate(startTime: Instant, endTime: Instant): Long {
        if (!hasAllPermissions()) return 0L
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records.lastOrNull()?.samples?.lastOrNull()?.beatsPerMinute ?: 0L
    }

    suspend fun readLatestWeight(startTime: Instant, endTime: Instant): Double {
        if (!hasAllPermissions()) return 0.0
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records.lastOrNull()?.weight?.inKilograms ?: 0.0
    }

    /**
     * Reads the most recent active exercise session to determine current activity type.
     * Returns null if no active session is found.
     */
    suspend fun readCurrentActivityType(startTime: Instant, endTime: Instant): Int? {
        if (!hasAllPermissions()) return null
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        // Find the most recent session that overlaps with current time
        val now = Instant.now()
        return response.records
            .filter { it.startTime <= now && it.endTime >= now }
            .maxByOrNull { it.startTime }
            ?.exerciseType
    }
}

