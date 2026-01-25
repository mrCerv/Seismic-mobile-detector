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
        // 1. Preprocess each channel independently
        // Note: RealtimePreprocessor currently takes FloatArray and returns FloatArray
        // We need to implement it such that it handles one channel.
        val processedX = preprocessor.preprocess(batch.x)
        val processedY = preprocessor.preprocess(batch.y)
        val processedZ = preprocessor.preprocess(batch.z)
        
        // 2. Interleave into [1000, 3] format -> [x0, y0, z0, x1, y1, z1, ...]
        val flatInput = FloatArray(1000 * 3)
        for (i in 0 until 1000) {
            flatInput[i * 3 + 0] = processedX[i]
            flatInput[i * 3 + 1] = processedY[i]
            flatInput[i * 3 + 2] = processedZ[i]
        }
        
        // 3. Inference
        val output = mlModel.doInference(flatInput) ?: return

        // 4. Logic
        val result = evaluatePrediction(output)
        
        _detectionFlow.emit(result)
    }

    private fun evaluatePrediction(output: ModelOutput): DetectionResult {
        // 1. Check Confidence
        if (output.isEarthquake < confidenceThreshold) {
            return DetectionResult.NoEvent
        }

        // 2. Check PGA
        // Note: Model predicts PGA. Sensor also has raw PGA? 
        // We use model's predicted PGA as requested in logic flow, 
        // OR we could calculate real PGA from sensor data.
        // Prompt says "pga: Dense(1, linear) -> Regressione". Use model output.
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
