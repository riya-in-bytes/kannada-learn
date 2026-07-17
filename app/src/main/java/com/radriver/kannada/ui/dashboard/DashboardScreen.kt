package com.radriver.kannada.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.radriver.kannada.util.TtsManager

/** Stateful entry point: owns the ViewModel, hoists state into the stateless content. */
@Composable
fun DashboardScreen(
    viewModelFactory: ViewModelProvider.Factory,
    ttsManager: TtsManager,
    onGoToQuiz: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardContent(
        uiState = uiState,
        ttsManager = ttsManager,
        onGoToQuiz = onGoToQuiz,
        modifier = modifier
    )
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    ttsManager: TtsManager,
    onGoToQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "title") {
            Text(
                text = stringResource(R.string.dashboard_title),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        uiState.wordOfTheDay?.let { word ->
            item(key = "wotd") {
                WordOfTheDayCard(word = word, ttsManager = ttsManager)
            }
        }

        item(key = "stats") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.LocalFireDepartment,
                    value = uiState.currentStreak.toString(),
                    label = stringResource(R.string.streak_label),
                    support = stringResource(R.string.longest_streak_label, uiState.longestStreak)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.WorkspacePremium,
                    value = stringResource(
                        R.string.words_mastered_value,
                        uiState.masteredWords,
                        uiState.totalWords
                    ),
                    label = stringResource(R.string.words_mastered_label),
                    support = stringResource(R.string.dashboard_hint, MASTERY_THRESHOLD)
                )
            }
        }

        if (uiState.dueForReview > 0) {
            item(key = "review") {
                ReviewDueCard(count = uiState.dueForReview, onClick = onGoToQuiz)
            }
        }

        item(key = "level-title") {
            Text(
                text = stringResource(R.string.level_progress_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(uiState.levelProgress, key = { "level-" + it.level.name }) { progress ->
            ProgressBarCard(
                title = progress.level.displayName,
                mastered = progress.mastered,
                total = progress.total
            )
        }

        item(key = "category-title") {
            Text(
                text = stringResource(R.string.category_progress_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(uiState.categoryProgress, key = { it.category.name }) { progress ->
            ProgressBarCard(
                title = progress.category.displayName,
                mastered = progress.mastered,
                total = progress.total
            )
        }
    }
}

@Composable
private fun WordOfTheDayCard(
    word: WordEntity,
    ttsManager: TtsManager,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = stringResource(R.string.dashboard_wotd_title),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = word.english,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = word.kannada,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = word.transliteration,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic
                )
            }
            SpeakerButton(text = word.kannada, ttsManager = ttsManager)
        }
    }
}

@Composable
private fun ReviewDueCard(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.dashboard_due_review, count),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f, fill = false)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    support: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = support,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/** One labeled mastery bar; used for both level rows and category rows. */
@Composable
private fun ProgressBarCard(
    title: String,
    mastered: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.words_mastered_value, mastered, total),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = {
                    if (total == 0) 0f
                    else mastered.toFloat() / total.toFloat()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
