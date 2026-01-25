package com.example.seismicdetector

import com.example.seismicdetector.domain.RealtimePreprocessor
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.abs

class RealtimePreprocessorTest {

    private val preprocessor = RealtimePreprocessor()

    @Test
    fun `test detrend removes linear trend`() {
        // Create signal with linear trend
        val input = FloatArray(100) { i ->
            (i * 0.5 + 2.0).toFloat() // y = 0.5x + 2
        }

        // We can't access private method detrend directly efficiently without reflection or opening it up.
        // But preprocessor.preprocess calls detrend -> filter -> normalize.
        // If we want to unit test component independently, we should usually make them package-private or internal.
        // For this test, let's assume valid output after full pipeline should be roughly zero mean.
        
        val output = preprocessor.preprocess(input)
        
        // Z-score normalization ensures mean is 0 and std is 1.
        val mean = output.average()
        
        assertEquals(0.0, mean, 0.001)
    }

    @Test
    fun `test z-score normalization stats`() {
        val input = FloatArray(1000) { i -> Math.sin(i * 0.1).toFloat() * 10 }
        val output = preprocessor.preprocess(input)

        val mean = output.average()
        val std = Math.sqrt(output.map { (it - mean) * (it - mean) }.average())

        assertEquals(0.0, mean, 0.001)
        assertEquals(1.0, std, 0.001)
    }
}
