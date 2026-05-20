package com.example.seismicdetector.ui.disclaimer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seismicdetector.R

@Composable
fun DisclaimerScreen(
    onAcceptWithSharing: () -> Unit,
    onAcceptLocalOnly: () -> Unit
) {
    val scrollState = rememberScrollState()
    val hasScrolledToBottom by remember { derivedStateOf { scrollState.value >= scrollState.maxValue - 10 } }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.disclaimer_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Box(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.disclaimer_body),
                    modifier = Modifier.verticalScroll(scrollState),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!hasScrolledToBottom) {
                Text(
                    "↓ Scorri per leggere / Scroll to read",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Button(
                onClick = onAcceptWithSharing,
                modifier = Modifier.fillMaxWidth(),
                enabled = hasScrolledToBottom
            ) {
                Text(stringResource(R.string.disclaimer_accept_research))
            }

            OutlinedButton(
                onClick = onAcceptLocalOnly,
                modifier = Modifier.fillMaxWidth(),
                enabled = hasScrolledToBottom
            ) {
                Text(stringResource(R.string.disclaimer_accept_local))
            }
        }
    }
}
