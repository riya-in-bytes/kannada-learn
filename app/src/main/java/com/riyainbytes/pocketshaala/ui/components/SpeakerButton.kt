package com.riyainbytes.pocketshaala.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riyainbytes.pocketshaala.R
import com.riyainbytes.pocketshaala.domain.model.Language
import com.riyainbytes.pocketshaala.util.TtsManager
import com.riyainbytes.pocketshaala.util.TtsStatus

/**
 * Speaker icon that voices [text] (native script) with [language]'s TTS
 * voice. [label] is what the learner sees for the word — used for the
 * accessibility description, so a hidden script is never read out. Renders
 * disabled when the device has no voice for the active language; the screen
 * hosting it is responsible for explaining why (see TtsUnavailableHint).
 */
@Composable
fun SpeakerButton(
    text: String,
    language: Language,
    label: String,
    ttsManager: TtsManager,
    modifier: Modifier = Modifier
) {
    val status by ttsManager.status.collectAsStateWithLifecycle()
    val enabled = status != TtsStatus.UNAVAILABLE

    IconButton(
        onClick = { ttsManager.speak(text, language) },
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = stringResource(R.string.speak_content_description, label),
            tint = if (enabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}
