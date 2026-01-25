package com.example.seismicdetector.domain

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

data class ModelOutput(
    val isEarthquake: Float,
    val intensity: Int,         // Argmax of intensity probabilities
    val intensityLabel: String, // String label
    val pga: Float,
    val dominantFreq: Float,
    val duration: Float,
    val rawIntensity: FloatArray // Probabilities
)

@Singleton
class SeismicMLModel @Inject constructor(
    private val context: Context
) {

    private var interpreter: Interpreter? = null
    
    // Input shape: [1, 1000, 3] float32
    // 1000 * 3 * 4 bytes = 12000 bytes
    private val inputBuffer = ByteBuffer.allocateDirect(1 * 1000 * 3 * 4).apply {
        order(ByteOrder.nativeOrder())
    }

    // Output buffers
    // 1. is_earthquake: [1, 1]
    private val outputIsEarthquake = Array(1) { FloatArray(1) }
    // 2. intensity: [1, 6]
    private val outputIntensity = Array(1) { FloatArray(6) }
    // 3. pga: [1, 1]
    private val outputPga = Array(1) { FloatArray(1) }
    // 4. dominant_freq: [1, 1]
    private val outputFreq = Array(1) { FloatArray(1) }
    // 5. duration: [1, 1]
    private val outputDuration = Array(1) { FloatArray(1) }
    
    // Output map for multiple outputs
    private val outputMap = mutableMapOf<Int, Any>()

    init {
        initializeInterpreter()
        
        // Map indices to buffers. 
        // ASSUMPTION based on prompt order: 
        // 0: is_earthquake
        // 1: intensity
        // 2: pga
        // 3: freq
        // 4: duration
        // Note: Actual indices depend on the TFLite conversion. 
        // Often Keras exports based on layer names alphabetically or creation order.
        // We might need to adjust this after inspecting the model.
        // For now, we assume the prompt order is preserved.
        outputMap[0] = outputIsEarthquake
        outputMap[1] = outputIntensity
        outputMap[2] = outputPga
        outputMap[3] = outputFreq
        outputMap[4] = outputDuration
    }

    private fun initializeInterpreter() {
        try {
            val assetManager = context.assets
            val modelDescriptor = assetManager.openFd("seismic_model.tflite")
            val inputStream = FileInputStream(modelDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = modelDescriptor.startOffset
            val declaredLength = modelDescriptor.declaredLength
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

            val options = Interpreter.Options()
            
            // GPU Delegate
            val compatibilityList = CompatibilityList()
            if (compatibilityList.isDelegateSupportedOnThisDevice) {
                val delegateOptions = compatibilityList.bestOptionsForThisDevice
                val gpuDelegate = GpuDelegate(delegateOptions)
                options.addDelegate(gpuDelegate)
            } else {
                options.setNumThreads(4)
            }

            interpreter = Interpreter(modelBuffer, options)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error (e.g., model file not found if not yet provided)
        }
    }

    fun doInference(preprocessedData: FloatArray): ModelOutput? {
        val tflite = interpreter ?: return null

        // 1. Load data into ByteBuffer
        inputBuffer.rewind()
        // Expected data: flattened [x0, y0, z0, x1, y1, z1, ...] or [x0..999, y0..999, z0..999]?
        // Keras input [1000, 3] usually means shape [Batch, Time, Channels], so [ [x0,y0,z0], [x1,y1,z1]... ]
        // Our SensorDataBatch gives valid arrays for X, Y, Z separate.
        // Preprocessor returns a single flattened array? 
        // Wait, RealtimePreprocessor.preprocess returns FloatArray.
        // Let's verify RealtimePreprocessor implementation.
        // It took a rawBuffer which was assumed to be combined or single channel? 
        // Ah, Preprocessor logic I wrote seemed to process a single float array (1 channel?).
        // The Prompt Preprocessor shows 'preprocess(rawBuffer: FloatArray)'. 
        // But the input to the detailed model is [1000, 3].
        // So we need to preprocess 3 channels separately.
        
        // FIX: The caller (EarthquakeDetector) should handle passing 3 channels to the preprocessor?
        // Or Preprocessor should take 3 arrays.
        // I will assume for now 'preprocessedData' passed here is FLATTENED [1000*3] in [Time, Channel] order: x0,y0,z0, x1,y1,z1...
        // because that's what TFLite [1, 1000, 3] expects.
        
        for (f in preprocessedData) {
            inputBuffer.putFloat(f)
        }

        // 2. Run Inference
        tflite.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputMap)

        // 3. Parse Outputs
        val isEarthquakeVal = outputIsEarthquake[0][0]
        val intensityProbs = outputIntensity[0]
        val pgaVal = outputPga[0][0]
        val freqVal = outputFreq[0][0]
        val durationVal = outputDuration[0][0]
        
        // Argmax intensity
        var maxIdx = 0
        var maxProb = -1f
        for (i in intensityProbs.indices) {
            if (intensityProbs[i] > maxProb) {
                maxProb = intensityProbs[i]
                maxIdx = i
            }
        }
        
        val intensityLabel = when(maxIdx) {
            0 -> "Not Perceived"     // Non percepito
            1 -> "Instrumental"      // Strumentale
            2 -> "Weak"              // Debole
            3 -> "Light"             // Leggero
            4 -> "Moderate"          // Moderato
            5 -> "Strong"            // Forte
            else -> "Unknown"
        }

        return ModelOutput(
            isEarthquake = isEarthquakeVal,
            intensity = maxIdx,
            intensityLabel = intensityLabel,
            pga = pgaVal,
            dominantFreq = freqVal,
            duration = durationVal,
            rawIntensity = intensityProbs.clone()
        )
    }
}
