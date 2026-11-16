package com.riyainbytes.pocketshaala.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.riyainbytes.pocketshaala.R
import com.riyainbytes.pocketshaala.data.local.entity.WordEntity
import com.riyainbytes.pocketshaala.ui.theme.CorrectGreen
import com.riyainbytes.pocketshaala.util.TtsManager

/**
 * One vocabulary card: English on top, the word large in the middle with its
 * speaker button, transliteration underneath (for a language that shows its
 * script — otherwise the large line already is the transliteration), and a
 * mastered badge once the word has been answered correctly enough times.
 */
@Composable
fun FlashCard(
    word: WordEntity,
    ttsManager: TtsManager,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word.english,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                if (word.isMastered) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = stringResource(R.string.mastered_badge),
                            tint = CorrectGreen
                        )
                        Text(
                            text = stringResource(R.string.mastered_badge),
                            style = MaterialTheme.typography.labelMedium,
                            color = CorrectGreen,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TargetPrimaryText(
                    word = word,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f, fill = false)
                )
                SpeakerButton(
                    text = word.script,
                    language = word.language,
                    label = word.displayText,
                    ttsManager = ttsManager
                )
            }

            TargetSecondaryText(
                word = word,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f)
            )
        }
    }
}
