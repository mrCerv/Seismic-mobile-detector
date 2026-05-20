package com.example.seismicdetector.ui.export

import com.example.seismicdetector.data.DetectionEntity
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

    fun exportToCsv(detections: List<DetectionEntity>, out: OutputStream) {
        out.bufferedWriter().use { writer ->
            writer.write("timestamp_iso,timestamp_unix,intensity,intensity_label,pga_m_s2,dominant_freq_hz,duration_s,confidence,waveform_path\n")
            detections.forEach { d ->
                writer.write("${isoFormat.format(Date(d.timestamp))},${d.timestamp},${d.intensity},${d.intensityLabel},${d.pga},${d.dominantFreq},${d.duration},${d.confidence},${d.waveformPath ?: ""}\n")
            }
        }
    }

    fun exportToJson(detections: List<DetectionEntity>, out: OutputStream) {
        out.bufferedWriter().use { writer ->
            writer.write("[\n")
            detections.forEachIndexed { i, d ->
                writer.write("""  {"timestamp_iso":"${isoFormat.format(Date(d.timestamp))}","timestamp_unix":${d.timestamp},"intensity":${d.intensity},"intensity_label":"${d.intensityLabel}","pga_m_s2":${d.pga},"dominant_freq_hz":${d.dominantFreq},"duration_s":${d.duration},"confidence":${d.confidence},"waveform_path":"${d.waveformPath ?: ""}"}""")
                if (i < detections.size - 1) writer.write(",")
                writer.newLine()
            }
            writer.write("]")
        }
    }
}
