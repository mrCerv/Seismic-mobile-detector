package com.example.seismicdetector.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class DetectionResult {
    data object NoEvent : DetectionResult()
    data class EarthquakeDetected(
        val confidence: Float,
        val intensity: String,
        val intensityIndex: Int,
        val pga: Float,
        val frequency: Float,
        val duration: Float,
        val timestamp: Long
    ) : DetectionResult()
}

@Singleton
class EarthquakeDetector @Inject constructor(
    private val preprocessor: RealtimePreprocessor,
    private val mlModel: SeismicMLModel
) {

    private val _detectionFlow = MutableSharedFlow<DetectionResult>()
    val detectionFlow: Flow<DetectionResult> = _detectionFlow.asSharedFlow()

    // Configurable thresholds
    var confidenceThreshold = 0.7f
    var minPgaThreshold = 0.05f

    suspend fun processBatch(batch: SensorDataBatch) {
        val processedLAX = preprocessor.preprocess(batch.linAccX)
        val processedLAY = preprocessor.preprocess(batch.linAccY)
        val processedLAZ = preprocessor.preprocess(batch.linAccZ)
        val processedGX = preprocessor.preprocessGyro(batch.gyroX)
        val processedGY = preprocessor.preprocessGyro(batch.gyroY)
        val processedGZ = preprocessor.preprocessGyro(batch.gyroZ)

        // Interleave into [1000, 6]: [la_x0, la_y0, la_z0, g_x0, g_y0, g_z0, la_x1, ...]
        val flatInput = FloatArray(1000 * 6)
        for (i in 0 until 1000) {
            flatInput[i * 6 + 0] = processedLAX[i]
            flatInput[i * 6 + 1] = processedLAY[i]
            flatInput[i * 6 + 2] = processedLAZ[i]
            flatInput[i * 6 + 3] = processedGX[i]
            flatInput[i * 6 + 4] = processedGY[i]
            flatInput[i * 6 + 5] = processedGZ[i]
        }

        val output = mlModel.doInference(flatInput) ?: return
        val result = evaluatePrediction(output)
        _detectionFlow.emit(result)
    }

    private fun evaluatePrediction(output: ModelOutput): DetectionResult {
        // 1. Check Confidence
        if (output.isEarthquake < confidenceThreshold) {
            return DetectionResult.NoEvent
        }

        // 2. Check PGA
        if (output.pga < minPgaThreshold) {
            return DetectionResult.NoEvent
        }

        // 3. Valid Event
        return DetectionResult.EarthquakeDetected(
            confidence = output.isEarthquake,
            intensity = output.intensityLabel,
            intensityIndex = output.intensity,
            pga = output.pga,
            frequency = output.dominantFreq,
            duration = output.duration,
            timestamp = System.currentTimeMillis()
        )
    }
}
