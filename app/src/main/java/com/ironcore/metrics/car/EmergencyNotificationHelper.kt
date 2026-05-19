package com.ironcore.metrics.car

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.car.app.notification.CarAppExtender
import androidx.car.app.notification.CarPendingIntent
import androidx.core.app.NotificationCompat
import com.ironcore.metrics.R

/**
 * Utility to send health alerts that surface on both phone and Android Auto.
 */
object EmergencyNotificationHelper {
    private const val CHANNEL_ID = "emergency_alerts"

    fun sendEmergencyAlert(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channel = NotificationChannel(CHANNEL_ID, "Emergency Alerts", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        // Intent to launch the car app in emergency mode
        val carIntent = Intent(context, IronCoreCarAppService::class.java).apply {
            putExtra("is_emergency", true)
        }
        val carPendingIntent = CarPendingIntent.getCarApp(context, 0, carIntent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("CRITICAL HEALTH ALERT")
            .setContentText("Extreme heart rate detected. Tap to open emergency controls.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .extend(
                CarAppExtender.Builder()
                    .setContentIntent(carPendingIntent)
                    .build()
            )

        notificationManager.notify(911, builder.build())
    }
}
