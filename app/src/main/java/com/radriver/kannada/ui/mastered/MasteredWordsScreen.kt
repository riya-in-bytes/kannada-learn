package com.radriver.kannada.ui.mastered

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.radriver.kannada.R
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.domain.model.MASTERY_THRESHOLD
import com.radriver.kannada.ui.components.SpeakerButton
import com.radriver.kannada.ui.components.TtsUnavailableHint
import com.radriver.kannada.util.TtsManager

/** Stateful entry point: owns the ViewModel, hoists state into the stateless content. */
@Composable
fun MasteredWordsScreen(
    viewModelFactory: ViewModelProvider.Factory,
    ttsManager: TtsManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MasteredWordsViewModel = viewModel(factory = viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MasteredWordsContent(
        uiState = uiState,
        ttsManager = ttsManager,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MasteredWordsContent(
    uiState: MasteredWordsUiState,
    ttsManager: TtsManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column {
                    Text(text = stringResource(R.string.mastered_words_title))
                    if (!uiState.isLoading) {
                        Text(
                            text = stringResource(R.string.mastered_words_count, uiState.total),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.mastered_words_back)
                    )
                }
            }
        )

        TtsUnavailableHint(
            ttsManager = ttsManager,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (!uiState.isLoading && uiState.groups.isEmpty()) {
            Text(
                text = stringResource(R.string.mastered_words_empty, MASTERY_THRESHOLD),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 48.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.groups.forEach { group ->
                    item(key = "header-" + group.category.name) {
                        Text(
                            text = group.category.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(group.words, key = { it.id }) { word ->
                        MasteredWordRow(word = word, ttsManager = ttsManager)
                    }
                }
            }
        }
    }
}

/**
 * A leaner row than [com.radriver.kannada.ui.components.FlashCard]: every word
 * on this screen is mastered, so the "Mastered" badge would be noise.
 */
@Composable
private fun MasteredWordRow(
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.kannada,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = word.english,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = word.transliteration,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f)
                )
            }
            SpeakerButton(text = word.kannada, ttsManager = ttsManager)
        }
    }
}
