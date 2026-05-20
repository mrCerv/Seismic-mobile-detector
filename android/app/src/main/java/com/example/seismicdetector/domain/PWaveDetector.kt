package com.example.seismicdetector.domain

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

data class PWaveAlert(
    val timestamp: Long,
    val staLtaRatio: Float,
    val channelZ: Float  // peak amplitude on Z axis at detection
)

@Singleton
class PWaveDetector @Inject constructor() {

    private val staSamples = 50   // 0.5s at 100Hz
    private val ltaSamples = 500  // 5s at 100Hz
    private var threshold = 3.0f

    private val ltaBuffer = FloatArray(ltaSamples)
    private var bufferPos = 0
    private var sampleCount = 0

    private val _pWaveFlow = MutableSharedFlow<PWaveAlert>()
    val pWaveFlow = _pWaveFlow.asSharedFlow()

    fun setThreshold(value: Float) { threshold = value }

    // Call this for every new sample on the Z (vertical) channel
    suspend fun processSample(zSample: Float) {
        val absSample = abs(zSample)
        ltaBuffer[bufferPos % ltaSamples] = absSample
        bufferPos++
        sampleCount++

        if (sampleCount < ltaSamples) return

        // Compute LTA (long-term average over full buffer)
        val lta = ltaBuffer.average().toFloat()
        if (lta < 1e-6f) return

        // Compute STA (short-term average over last staSamples)
        var staSum = 0f
        for (i in 0 until staSamples) {
            val idx = (bufferPos - 1 - i + ltaSamples) % ltaSamples
            staSum += ltaBuffer[idx]
        }
        val sta = staSum / staSamples

        val ratio = sta / lta
        if (ratio > threshold) {
            _pWaveFlow.emit(PWaveAlert(
                timestamp = System.currentTimeMillis(),
                staLtaRatio = ratio,
                channelZ = zSample
            ))
            // Reset STA period to avoid repeated triggers
            repeat(staSamples) { i ->
                ltaBuffer[(bufferPos - 1 - i + ltaSamples) % ltaSamples] = lta
            }
        }
    }

    fun reset() {
        ltaBuffer.fill(0f)
        bufferPos = 0
        sampleCount = 0
    }
}
