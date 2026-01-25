package com.example.seismicdetector.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seismicdetector.data.AppSettings
import com.example.seismicdetector.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    val settings: StateFlow<AppSettings> = preferencesManager.settings

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            preferencesManager.updateSettings(newSettings)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Detection Threshold
            Column {
                Text("Detection Confidence Threshold: ${(settings.detectionThreshold * 100).toInt()}%")
                Slider(
                    value = settings.detectionThreshold,
                    onValueChange = { viewModel.updateSettings(settings.copy(detectionThreshold = it)) },
                    valueRange = 0.5f..0.95f,
                    steps = 9 // 0.5, 0.55, ...
                )
                Text(
                    "Minimum confidence required to trigger an alert.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Divider()

            // Toggles
            SettingSwitch(
                title = "Notifications",
                checked = settings.enableNotifications,
                onCheckedChange = { viewModel.updateSettings(settings.copy(enableNotifications = it)) }
            )
            
            SettingSwitch(
                title = "Vibration",
                checked = settings.enableVibration,
                onCheckedChange = { viewModel.updateSettings(settings.copy(enableVibration = it)) }
            )
            
             SettingSwitch(
                title = "Sound",
                checked = settings.enableSound,
                onCheckedChange = { viewModel.updateSettings(settings.copy(enableSound = it)) }
            )
            
             SettingSwitch(
                title = "Record Waveform",
                description = "Save CSV data for detected events",
                checked = settings.recordWaveform,
                onCheckedChange = { viewModel.updateSettings(settings.copy(recordWaveform = it)) }
            )
        }
    }
}

@Composable
fun SettingSwitch(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (description != null) {
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
