package com.example.seismicdetector.domain

import kotlin.math.sqrt

/**
 * Handles real-time signal preprocessing for seismic data.
 * Pipeline: Detrend -> Butterworth Bandpass -> Z-Score Normalization
 */
class RealtimePreprocessor {

    // Optimized Butterworth Bandpass coefficients (0.5 - 25 Hz @ 100 Hz)
    // Structure: b0, b1, b2, a1, a2 (a0 is assumed 1.0)
    private val sosSections = arrayOf(
        doubleArrayOf(0.97803, -1.95606, 0.97803, -1.95558, 0.95654), // High-pass 0.5Hz
        doubleArrayOf(0.29289, 0.58579, 0.29289, -0.00000, 0.17157)   // Low-pass 25Hz
    )

    // Gyroscope SOS filter coefficients (0.5 - 25 Hz @ 100 Hz) — same filter, seismic gyro range is similar
    private val sosSectionsGyro = arrayOf(
        doubleArrayOf(0.97803, -1.95606, 0.97803, -1.95558, 0.95654), // High-pass 0.5Hz
        doubleArrayOf(0.29289, 0.58579, 0.29289, -0.00000, 0.17157)   // Low-pass 25Hz
    )

    fun preprocess(rawBuffer: FloatArray): FloatArray {
        if (rawBuffer.isEmpty()) return rawBuffer

        // 1. Detrend (Linear) - In-place for performance
        val detrended = detrend(rawBuffer)

        // 2. Bandpass filter (0.5 - 25 Hz)
        val filtered = applySosFilter(detrended, sosSections)

        // 3. Z-score normalization
        return zScoreNormalize(filtered)
    }

    fun preprocessGyro(rawBuffer: FloatArray): FloatArray {
        if (rawBuffer.isEmpty()) return rawBuffer

        // 1. Detrend (Linear)
        val detrended = detrend(rawBuffer)

        // 2. Bandpass filter (0.5 - 25 Hz) using gyro filter coefficients
        val filtered = applySosFilter(detrended, sosSectionsGyro)

        // 3. Z-score normalization
        return zScoreNormalize(filtered)
    }

    private fun detrend(data: FloatArray): FloatArray {
        val n = data.size
        if (n < 2) return data

        var sumX = 0.0
        var sumY = 0.0
        var sumXY = 0.0
        var sumXX = 0.0

        for (i in data.indices) {
            val x = i.toDouble()
            val y = data[i].toDouble()
            sumX += x
            sumY += y
            sumXY += x * y
            sumXX += x * x
        }

        val slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
        val intercept = (sumY - slope * sumX) / n

        val result = FloatArray(n)
        for (i in data.indices) {
            result[i] = (data[i] - (slope * i + intercept)).toFloat()
        }
        return result
    }

    private fun applySosFilter(data: FloatArray, sos: Array<DoubleArray>): FloatArray {
        var currentData = data

        for (section in sos) {
            val output = FloatArray(currentData.size)
            var w1 = 0.0
            var w2 = 0.0

            val b0 = section[0]; val b1 = section[1]; val b2 = section[2]
            val a1 = section[3]; val a2 = section[4]

            for (i in currentData.indices) {
                val x = currentData[i].toDouble()
                val w = x - a1 * w1 - a2 * w2
                output[i] = (b0 * w + b1 * w1 + b2 * w2).toFloat()
                w2 = w1
                w1 = w
            }
            currentData = output
        }
        return currentData
    }

    private fun zScoreNormalize(data: FloatArray): FloatArray {
        val n = data.size
        if (n == 0) return data

        var sum = 0.0
        var sumSq = 0.0
        for (x in data) {
            sum += x
            sumSq += x * x
        }

        val mean = sum / n
        val variance = (sumSq / n) - (mean * mean)
        val std = sqrt(if (variance > 0) variance else 0.0)

        if (std < 1e-6) return FloatArray(n) // Avoid division by zero or noise amplification

        val result = FloatArray(n)
        for (i in data.indices) {
            result[i] = ((data[i] - mean) / std).toFloat()
        }
        return result
    }
}
