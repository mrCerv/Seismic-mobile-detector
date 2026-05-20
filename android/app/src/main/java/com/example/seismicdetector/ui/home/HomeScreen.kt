package com.example.seismicdetector.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seismicdetector.domain.DetectionResult
import com.example.seismicdetector.domain.SensorDataBatch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌍 Seismic Detector") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.Info, contentDescription = "History")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Card
            StatusCard(
                isMonitoring = uiState.isMonitoring,
                sampleRate = uiState.currentSampleRate,
                onToggleMonitoring = viewModel::toggleMonitoring
            )

            // Real-time Graph
            Text(
                "Real-time Linear Acceleration (X/Y/Z)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RealtimeGraphCard(
                sensorData = uiState.sensorData
            )

            // Last Detection Card
            AnimatedVisibility(
                visible = uiState.lastDetection != null,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                uiState.lastDetection?.let { detection ->
                    DetectionCard(detection)
                }
            }
        }
    }
}

@Composable
fun StatusCard(
    isMonitoring: Boolean,
    sampleRate: Float,
    onToggleMonitoring: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMonitoring) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = if (isMonitoring) "🟢 MONITORING" else "🔴 STOPPED",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isMonitoring) Color(0xFF006400) else Color.Red
                    )
                    Text("Sampling: %.1f Hz".format(sampleRate))
                }
                
                Button(
                    onClick = onToggleMonitoring,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMonitoring) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        if (isMonitoring) Icons.Default.Warning else Icons.Default.PlayArrow, // Stop icon? Warning works for Stop
                        contentDescription = if (isMonitoring) "Stop" else "Start"
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(if (isMonitoring) "Stop" else "Start")
                }
            }
        }
    }
}

@Composable
fun RealtimeGraphCard(sensorData: SensorDataBatch?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Fixed height for graph
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(8.dp)
        ) {
            if (sensorData == null) {
                Text("Waiting for data...", color = Color.White, modifier = Modifier.align(Alignment.Center))
            } else {
                // Drawing 3 lines: X (Red), Y (Green), Z (Blue)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val midY = height / 2
                    
                    // Simple auto-scale or fixed scale?
                    // Accelerometer usually covers -10 to +10 m/s^2 (gravity 9.8)
                    // If we remove gravity (detrend/highpass), it's around 0.
                    // Let's assume raw data includes gravity for now, 
                    // or let's visualize normalized if possible?
                    // The batch.raw is raw. Scale: 1g = 9.8. Range approx +/- 2g (+/- 20).
                    val scaleY = height / 40f // 20 units up, 20 down
                    
                    // Draw X
                    val pathX = Path()
                    // Draw Y
                    val pathY = Path()
                    // Draw Z
                    val pathZ = Path()
                    
                    val stepX = width / sensorData.linAccX.size.coerceAtLeast(1)

                    var started = false

                    // Subsample for performance if needed (1000 points is fine for Canvas)
                    val points = sensorData.linAccX.size
                    for (i in 0 until points step 2) {
                        val xPos = i * stepX
                        // Raw values
                        val yX = midY - (sensorData.linAccX[i] * scaleY)
                        val yY = midY - (sensorData.linAccY[i] * scaleY)
                        val yZ = midY - (sensorData.linAccZ[i] * scaleY)
                        
                        if (!started) {
                            pathX.moveTo(xPos, yX)
                            pathY.moveTo(xPos, yY)
                            pathZ.moveTo(xPos, yZ)
                            started = true
                        } else {
                            pathX.lineTo(xPos, yX)
                            pathY.lineTo(xPos, yY)
                            pathZ.lineTo(xPos, yZ)
                        }
                    }
                    
                    drawPath(pathX, Color.Red, style = Stroke(width = 2f))
                    drawPath(pathY, Color.Green, style = Stroke(width = 2f))
                    drawPath(pathZ, Color.Cyan, style = Stroke(width = 2f))
                }
                
                // Legend
                Column(modifier = Modifier.align(Alignment.TopEnd)) {
                    Text("linAccX", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    Text("linAccY", color = Color.Green, style = MaterialTheme.typography.bodySmall)
                    Text("linAccZ", color = Color.Cyan, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun DetectionCard(detection: DetectionResult.EarthquakeDetected) {
    val intensityColor = when(detection.intensityIndex) {
        0, 1 -> Color(0xFF4CAF50) // Green
        2, 3 -> Color(0xFFFFC107) // Amber
        else -> Color(0xFFF44336) // Red
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = intensityColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
             Row(verticalAlignment = Alignment.CenterVertically) {
                 Icon(Icons.Default.Warning, contentDescription = "Alert", tint = Color.White)
                 Spacer(Modifier.size(8.dp))
                 Text("EARTHQUAKE DETECTED", style = MaterialTheme.typography.headlineSmall, color = Color.White)
             }
            Spacer(Modifier.size(8.dp))
            Text("Intensity: ${detection.intensity}", color = Color.White, style = MaterialTheme.typography.titleLarge)
            Text("PGA: %.2f m/s²".format(detection.pga), color = Color.White)
            Text("Confidence: %.0f%%".format(detection.confidence * 100), color = Color.White)
            Text("Duration: %.1fs".format(detection.duration), color = Color.White)
        }
    }
}
