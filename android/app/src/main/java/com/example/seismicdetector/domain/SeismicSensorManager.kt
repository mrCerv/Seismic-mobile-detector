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
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val bufferSize = 1000
    private val bufferX = FloatArray(bufferSize)
    private val bufferY = FloatArray(bufferSize)
    private val bufferZ = FloatArray(bufferSize)
    private var headIndex = 0
    private var isBufferFull = false

    private val _sensorDataFlow = MutableSharedFlow<SensorDataBatch>(
        replay = 0,
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sensorDataFlow = _sensorDataFlow.asSharedFlow()

    private var sampleCounter = 0
    private val emitInterval = 100 // Emit every 1 second (at 100Hz)
    
    var isMonitoring = false
        private set
    
    fun startMonitoring() {
        if (isMonitoring || accelerometer == null) return
        
        // Target 100Hz = 10,000 microseconds
        val samplingPeriodUs = 10000 
        sensorManager.registerListener(this, accelerometer, samplingPeriodUs)
        isMonitoring = true
        resetBuffers()
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(this)
        isMonitoring = false
    }

    private fun resetBuffers() {
        headIndex = 0
        isBufferFull = false
        sampleCounter = 0
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        bufferX[headIndex] = event.values[0]
        bufferY[headIndex] = event.values[1]
        bufferZ[headIndex] = event.values[2]

        headIndex = (headIndex + 1) % bufferSize
        if (headIndex == 0) isBufferFull = true

        sampleCounter++
        if (isBufferFull && sampleCounter >= emitInterval) {
            emitCurrentWindow()
            sampleCounter = 0
        }
    }

    private fun emitCurrentWindow() {
        val orderedX = FloatArray(bufferSize)
        val orderedY = FloatArray(bufferSize)
        val orderedZ = FloatArray(bufferSize)

        val tailLen = bufferSize - headIndex
        
        System.arraycopy(bufferX, headIndex, orderedX, 0, tailLen)
        System.arraycopy(bufferX, 0, orderedX, tailLen, headIndex)
        
        System.arraycopy(bufferY, headIndex, orderedY, 0, tailLen)
        System.arraycopy(bufferY, 0, orderedY, tailLen, headIndex)
        
        System.arraycopy(bufferZ, headIndex, orderedZ, 0, tailLen)
        System.arraycopy(bufferZ, 0, orderedZ, tailLen, headIndex)

        _sensorDataFlow.tryEmit(SensorDataBatch(orderedX, orderedY, orderedZ))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

data class SensorDataBatch(
    val x: FloatArray,
    val y: FloatArray,
    val z: FloatArray
)
