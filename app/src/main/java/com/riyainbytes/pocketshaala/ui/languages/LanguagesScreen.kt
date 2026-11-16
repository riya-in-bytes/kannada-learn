package com.riyainbytes.pocketshaala.ui.languages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riyainbytes.pocketshaala.R
import com.riyainbytes.pocketshaala.data.repository.LanguageSummary
import com.riyainbytes.pocketshaala.domain.model.Language

/** Stateful entry point: owns the ViewModel, hoists state into the stateless content. */
@Composable
fun LanguagesScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onSwitched: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LanguagesViewModel = viewModel(factory = viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LanguagesContent(
        uiState = uiState,
        onSwitch = { language ->
            viewModel.switchTo(language)
            onSwitched()
        },
        modifier = modifier
    )
}

@Composable
private fun LanguagesContent(
    uiState: LanguagesUiState,
    onSwitch: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "title") {
            Column {
                Text(
                    text = stringResource(R.string.languages_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(R.string.languages_subtitle),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        items(uiState.summaries, key = { it.language.name }) { summary ->
            LanguageCard(
                summary = summary,
                isActive = summary.language == uiState.active,
                onSwitch = { onSwitch(summary.language) }
            )
        }

        item(key = "default-note") {
            Text(
                text = stringResource(R.string.languages_default_note, Language.DEFAULT.displayName),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * One language's headline progress. The active card is highlighted and
 * labelled; the others offer a Switch button.
 */
@Composable
private fun LanguageCard(
    summary: LanguageSummary,
    isActive: Boolean,
    onSwitch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = summary.language
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isActive) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Only a language that shows its script names itself in it.
                    language.nativeName?.let { native ->
                        Text(text = native, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text = language.displayName, style = MaterialTheme.typography.titleLarge)
                }
                if (isActive) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.languages_active),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = {
                        if (summary.totalWords == 0) 0f
                        else summary.masteredWords.toFloat() / summary.totalWords.toFloat()
                    },
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(
                        R.string.words_mastered_value,
                        summary.masteredWords,
                        summary.totalWords
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.languages_due, summary.dueForReview),
                style = MaterialTheme.typography.bodySmall
            )
            if (!language.showsScript) {
                Text(
                    text = stringResource(R.string.languages_romanized_only),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!isActive) {
                val switchDescription =
                    stringResource(R.string.languages_switch_description, language.displayName)
                Button(
                    onClick = onSwitch,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                        .semantics { contentDescription = switchDescription }
                ) {
                    Text(stringResource(R.string.languages_switch))
                }
            }
        }
    }
}
