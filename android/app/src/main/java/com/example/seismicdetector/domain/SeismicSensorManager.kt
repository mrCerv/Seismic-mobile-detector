package com.example.seismicdetector.domain

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeismicSensorManager @Inject constructor(
    private val context: Context
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val linearAccelSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val gyroscopeSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val bufferSize = 1000

    // Linear acceleration buffers
    private val bufferLinAccX = FloatArray(bufferSize)
    private val bufferLinAccY = FloatArray(bufferSize)
    private val bufferLinAccZ = FloatArray(bufferSize)
    private var linAccHeadIndex = 0
    private var isLinAccBufferFull = false

    // Gyroscope buffers
    private val bufferGyroX = FloatArray(bufferSize)
    private val bufferGyroY = FloatArray(bufferSize)
    private val bufferGyroZ = FloatArray(bufferSize)
    private var gyroHeadIndex = 0
    private var isGyroBufferFull = false

    private val _sensorDataFlow = MutableSharedFlow<SensorDataBatch>(
        replay = 0,
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sensorDataFlow = _sensorDataFlow.asSharedFlow()

    private var sampleCounter = 0
    private val emitInterval = 100 // Emit every 1 second (at 100Hz)

    // Fixed sample rate property
    val currentSampleRate: Float = 100f

    var isMonitoring = false
        private set

    fun startMonitoring() {
        if (isMonitoring) return

        // Target 100Hz = 10,000 microseconds
        val samplingPeriodUs = 10000

        linearAccelSensor?.let {
            sensorManager.registerListener(this, it, samplingPeriodUs)
        }

        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, samplingPeriodUs)
        }

        isMonitoring = true
        resetBuffers()
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(this)
        isMonitoring = false
    }

    private fun resetBuffers() {
        linAccHeadIndex = 0
        isLinAccBufferFull = false
        gyroHeadIndex = 0
        isGyroBufferFull = false
        sampleCounter = 0
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                bufferLinAccX[linAccHeadIndex] = event.values[0]
                bufferLinAccY[linAccHeadIndex] = event.values[1]
                bufferLinAccZ[linAccHeadIndex] = event.values[2]

                linAccHeadIndex = (linAccHeadIndex + 1) % bufferSize
                if (linAccHeadIndex == 0) isLinAccBufferFull = true

                sampleCounter++
                val gyroReady = isGyroBufferFull || gyroscopeSensor == null
                if (isLinAccBufferFull && gyroReady && sampleCounter >= emitInterval) {
                    emitCurrentWindow()
                    sampleCounter = 0
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                bufferGyroX[gyroHeadIndex] = event.values[0]
                bufferGyroY[gyroHeadIndex] = event.values[1]
                bufferGyroZ[gyroHeadIndex] = event.values[2]

                gyroHeadIndex = (gyroHeadIndex + 1) % bufferSize
                if (gyroHeadIndex == 0) isGyroBufferFull = true
            }
        }
    }

    private fun emitCurrentWindow() {
        val orderedLinAccX = FloatArray(bufferSize)
        val orderedLinAccY = FloatArray(bufferSize)
        val orderedLinAccZ = FloatArray(bufferSize)
        val orderedGyroX = FloatArray(bufferSize)
        val orderedGyroY = FloatArray(bufferSize)
        val orderedGyroZ = FloatArray(bufferSize)

        // Reorder linear acceleration buffers
        val linAccTailLen = bufferSize - linAccHeadIndex
        System.arraycopy(bufferLinAccX, linAccHeadIndex, orderedLinAccX, 0, linAccTailLen)
        System.arraycopy(bufferLinAccX, 0, orderedLinAccX, linAccTailLen, linAccHeadIndex)

        System.arraycopy(bufferLinAccY, linAccHeadIndex, orderedLinAccY, 0, linAccTailLen)
        System.arraycopy(bufferLinAccY, 0, orderedLinAccY, linAccTailLen, linAccHeadIndex)

        System.arraycopy(bufferLinAccZ, linAccHeadIndex, orderedLinAccZ, 0, linAccTailLen)
        System.arraycopy(bufferLinAccZ, 0, orderedLinAccZ, linAccTailLen, linAccHeadIndex)

        // Reorder gyroscope buffers (or fill with zeros if gyroscope not available)
        if (gyroscopeSensor != null && isGyroBufferFull) {
            val gyroTailLen = bufferSize - gyroHeadIndex
            System.arraycopy(bufferGyroX, gyroHeadIndex, orderedGyroX, 0, gyroTailLen)
            System.arraycopy(bufferGyroX, 0, orderedGyroX, gyroTailLen, gyroHeadIndex)

            System.arraycopy(bufferGyroY, gyroHeadIndex, orderedGyroY, 0, gyroTailLen)
            System.arraycopy(bufferGyroY, 0, orderedGyroY, gyroTailLen, gyroHeadIndex)

            System.arraycopy(bufferGyroZ, gyroHeadIndex, orderedGyroZ, 0, gyroTailLen)
            System.arraycopy(bufferGyroZ, 0, orderedGyroZ, gyroTailLen, gyroHeadIndex)
        }
        // If gyroscope unavailable, orderedGyro arrays remain zero-filled (default FloatArray)

        _sensorDataFlow.tryEmit(
            SensorDataBatch(
                linAccX = orderedLinAccX,
                linAccY = orderedLinAccY,
                linAccZ = orderedLinAccZ,
                gyroX = orderedGyroX,
                gyroY = orderedGyroY,
                gyroZ = orderedGyroZ
            )
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

data class SensorDataBatch(
    val linAccX: FloatArray,
    val linAccY: FloatArray,
    val linAccZ: FloatArray,
    val gyroX: FloatArray,
    val gyroY: FloatArray,
    val gyroZ: FloatArray
)
