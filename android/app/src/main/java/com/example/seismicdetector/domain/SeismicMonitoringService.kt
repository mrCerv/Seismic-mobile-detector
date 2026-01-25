package com.example.seismicdetector.domain

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import com.example.seismicdetector.data.SeismicRepository
import com.example.seismicdetector.ui.SeismicNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SeismicMonitoringService : Service() {

    @Inject lateinit var sensorManager: SeismicSensorManager
    @Inject lateinit var detector: EarthquakeDetector
    @Inject lateinit var repository: SeismicRepository
    @Inject lateinit var notificationManager: SeismicNotificationManager

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
    
    private var wakeLock: PowerManager.WakeLock? = null
    private var collectingJob: Job? = null
    private var detectionJob: Job? = null

    companion object {
        private const val WAKE_LOCK_TAG = "SeismicDetector::ServiceWakeLock"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, notificationManager.getServiceNotification())
        
        // Acquire WakeLock
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG).apply {
            acquire()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMonitoring()
        return START_STICKY
    }

    private fun startMonitoring() {
        if (!sensorManager.isMonitoring) {
            sensorManager.startMonitoring()
        }

        // Collect Sensor Data and Pipe to Detector
        collectingJob?.cancel()
        collectingJob = serviceScope.launch {
            sensorManager.sensorDataFlow.collect { batch ->
                 // Process batch in detector
                 detector.processBatch(batch)
            }
        }

        // Listen for Detections
        detectionJob?.cancel()
        detectionJob = serviceScope.launch {
            detector.detectionFlow.collect { result ->
                if (result is DetectionResult.EarthquakeDetected) {
                    handleDetection(result)
                }
            }
        }
    }

    private fun handleDetection(result: DetectionResult.EarthquakeDetected) {
        serviceScope.launch {
            // 1. Save to DB
            repository.saveDetection(result)
            
            // 2. Show Alert
            notificationManager.showEarthquakeAlert(result)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.stopMonitoring()
        serviceJob.cancel()
        
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
