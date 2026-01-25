package com.example.seismicdetector

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.seismicdetector.ui.SeismicNotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SeismicApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Alerts Channel (High Priority)
            val alertChannel = NotificationChannel(
                SeismicNotificationManager.CHANNEL_ALERTS,
                "Seismic Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority notifications for detected seismic events"
                enableVibration(true)
                setShowBadge(true)
            }

            // 2. Service Channel (Low Priority)
            val serviceChannel = NotificationChannel(
                SeismicNotificationManager.CHANNEL_SERVICE,
                "Monitoring Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background monitoring status"
                setShowBadge(false)
            }

            notificationManager.createNotificationChannel(alertChannel)
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }
}
