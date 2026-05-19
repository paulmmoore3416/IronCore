package com.ironcore.metrics.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ironcore.metrics.MainActivity
import com.ironcore.metrics.R
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Enhancement 2: Smart Notification System with Context Awareness
 * Provides intelligent notifications based on user activity patterns, time of day,
 * and health metrics. Avoids notification fatigue through smart scheduling.
 */
class SmartNotificationManager(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID_HYDRATION = "hydration_reminders"
        private const val CHANNEL_ID_WORKOUT = "workout_reminders"
        private const val CHANNEL_ID_RECOVERY = "recovery_alerts"
        private const val CHANNEL_ID_VITALS = "vitals_monitoring"
        
        private const val NOTIFICATION_ID_HYDRATION = 1001
        private const val NOTIFICATION_ID_WORKOUT = 1002
        private const val NOTIFICATION_ID_RECOVERY = 1003
        private const val NOTIFICATION_ID_VITALS = 1004
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Hydration Channel
        val hydrationChannel = NotificationChannel(
            CHANNEL_ID_HYDRATION,
            "Hydration Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Smart reminders to stay hydrated based on your activity"
            enableVibration(true)
        }
        
        // Workout Channel
        val workoutChannel = NotificationChannel(
            CHANNEL_ID_WORKOUT,
            "Workout Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Intelligent workout suggestions based on your schedule"
            enableVibration(true)
        }
        
        // Recovery Channel
        val recoveryChannel = NotificationChannel(
            CHANNEL_ID_RECOVERY,
            "Recovery Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Important recovery and rest recommendations"
            enableVibration(true)
        }
        
        // Vitals Monitoring Channel
        val vitalsChannel = NotificationChannel(
            CHANNEL_ID_VITALS,
            "Vitals Monitoring",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Critical health vitals alerts"
            enableVibration(true)
            enableLights(true)
        }
        
        notificationManager.createNotificationChannels(listOf(
            hydrationChannel,
            workoutChannel,
            recoveryChannel,
            vitalsChannel
        ))
    }
    
    /**
     * Context-aware hydration reminder
     * Only sends during waking hours and considers recent activity
     */
    fun sendHydrationReminder(currentHydration: Int, target: Int, lastLoggedMinutesAgo: Int) {
        val currentTime = LocalTime.now()
        
        // Don't send notifications during sleep hours (10 PM - 7 AM)
        if (currentTime.hour < 7 || currentTime.hour >= 22) {
            return
        }
        
        // Only remind if significantly behind target and haven't logged recently
        val progress = currentHydration.toFloat() / target
        if (progress < 0.5 && lastLoggedMinutesAgo > 120) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "hydration")
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_HYDRATION)
                .setSmallIcon(R.drawable.ic_ironcore_logo)
                .setContentTitle("💧 Hydration Check")
                .setContentText("You're at ${(progress * 100).toInt()}% of your daily goal. Time to hydrate!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(
                    R.drawable.ic_ironcore_logo,
                    "Log 250ml",
                    createHydrationActionIntent(250)
                )
                .build()
            
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_HYDRATION, notification)
        }
    }
    
    /**
     * Smart workout reminder based on user patterns
     * Considers time of day, last workout, and recovery status
     */
    fun sendWorkoutReminder(
        hoursSinceLastWorkout: Int,
        recoveryScore: Int,
        preferredWorkoutTime: LocalTime = LocalTime.of(17, 0) // Default 5 PM
    ) {
        val currentTime = LocalTime.now()
        val currentHour = currentTime.hour
        
        // Only send during typical workout windows
        if (currentHour < 6 || currentHour >= 22) {
            return
        }
        
        // Check if it's near preferred workout time (within 1 hour)
        val isNearPreferredTime = Math.abs(currentHour - preferredWorkoutTime.hour) <= 1
        
        // Send reminder if:
        // 1. It's been more than 24 hours since last workout
        // 2. Recovery score is good (>60%)
        // 3. It's near preferred workout time
        if (hoursSinceLastWorkout > 24 && recoveryScore > 60 && isNearPreferredTime) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "workout")
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_WORKOUT)
                .setSmallIcon(R.drawable.ic_ironcore_logo)
                .setContentTitle("💪 Ready to Train?")
                .setContentText("Your recovery is at $recoveryScore%. Perfect time for a workout!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_WORKOUT, notification)
        }
    }
    
    /**
     * Recovery alert for overtraining detection
     */
    fun sendRecoveryAlert(recoveryScore: Int, consecutiveHighIntensityDays: Int) {
        // Alert if recovery is low and user has been training hard
        if (recoveryScore < 40 && consecutiveHighIntensityDays >= 3) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "dashboard")
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_RECOVERY)
                .setSmallIcon(R.drawable.ic_ironcore_logo)
                .setContentTitle("⚠️ Recovery Alert")
                .setContentText("Your body needs rest. Recovery score: $recoveryScore%. Consider a rest day.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Your recovery score is $recoveryScore% after $consecutiveHighIntensityDays days of intense training. Your body needs adequate rest to prevent overtraining and injury."))
                .build()
            
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_RECOVERY, notification)
        }
    }
    
    /**
     * Critical vitals alert
     */
    fun sendVitalsAlert(metric: String, value: String, severity: VitalsSeverity) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "dashboard")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val (title, priority) = when (severity) {
            VitalsSeverity.WARNING -> "⚠️ Vitals Warning" to NotificationCompat.PRIORITY_HIGH
            VitalsSeverity.CRITICAL -> "🚨 CRITICAL ALERT" to NotificationCompat.PRIORITY_MAX
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_VITALS)
            .setSmallIcon(R.drawable.ic_ironcore_logo)
            .setContentTitle(title)
            .setContentText("$metric: $value - Immediate attention recommended")
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_VITALS, notification)
    }
    
    /**
     * Motivational notification based on achievements
     */
    fun sendAchievementNotification(achievement: String, description: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_WORKOUT)
            .setSmallIcon(R.drawable.ic_ironcore_logo)
            .setContentTitle("🏆 Achievement Unlocked!")
            .setContentText("$achievement: $description")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
    
    private fun createHydrationActionIntent(amount: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = "ADD_HYDRATION"
            putExtra("amount", amount)
        }
        return PendingIntent.getActivity(
            context,
            amount,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}

enum class VitalsSeverity {
    WARNING,
    CRITICAL
}
