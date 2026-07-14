package com.radriver.kannada.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radriver.kannada.R
import com.radriver.kannada.util.TtsManager
import com.radriver.kannada.util.TtsStatus

/**
 * Inline banner shown only when the device lacks a Kannada TTS voice,
 * explaining why speaker buttons are disabled and how to fix it.
 */
@Composable
fun TtsUnavailableHint(
    ttsManager: TtsManager,
    modifier: Modifier = Modifier
) {
    val status by ttsManager.status.collectAsStateWithLifecycle()
    if (status != TtsStatus.UNAVAILABLE) return

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                text = stringResource(R.string.tts_unavailable),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
