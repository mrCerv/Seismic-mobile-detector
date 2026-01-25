package com.example.seismicdetector.data

import com.example.seismicdetector.domain.DetectionResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeismicRepository @Inject constructor(
    private val detectionDao: DetectionDao
) {

    val recentDetections: Flow<List<DetectionEntity>> = detectionDao.getRecentDetections()

    suspend fun saveDetection(result: DetectionResult.EarthquakeDetected, waveformPath: String? = null) {
        val entity = DetectionEntity(
            timestamp = result.timestamp,
            confidence = result.confidence,
            intensity = result.intensityIndex,
            intensityLabel = result.intensity,
            pga = result.pga,
            dominantFreq = result.frequency,
            duration = result.duration,
            waveformPath = waveformPath
        )
        detectionDao.insertDetection(entity)
    }

    suspend fun cleanupOldDetections(maxAgeMillis: Long) {
        val cutoff = System.currentTimeMillis() - maxAgeMillis
        detectionDao.deleteOldDetections(cutoff)
    }
}
