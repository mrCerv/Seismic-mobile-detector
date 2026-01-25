package com.example.seismicdetector.ui

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.seismicdetector.MainActivity
import com.example.seismicdetector.R
import com.example.seismicdetector.domain.DetectionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SeismicNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "SEISMIC_ALERTS"
        const val SERVICE_CHANNEL_ID = "SEISMIC_SERVICE"
    }

    fun showEarthquakeAlert(detection: DetectionResult.EarthquakeDetected) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val vibrationPattern = longArrayOf(0, 500, 200, 500)

        // Color based on intensity (approximate)
        // 0-1: Local weak -> Default
        // 2-3: Yellow
        // 4-5: Red
        
        // This is a high priority notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // Ensure this exists or use system icon
            .setContentTitle("⚠️ TERREMOTO RILEVATO")
            .setContentText("Intensità: ${detection.intensity} - PGA: ${detection.pga} m/s²")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(vibrationPattern)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Use a unique ID to stack notifications or defined ID to update
        val notificationId = (System.currentTimeMillis() % 10000).toInt()
        notificationManager.notify(notificationId, builder.build())
    }
    
    // Notification for the foreground service
    fun getServiceNotification(): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, CHANNEL_ID) // Using same channel for simplicity or create separate LOW priority
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Seismic Detector Active")
            .setContentText("Monitoring sensors in background...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
