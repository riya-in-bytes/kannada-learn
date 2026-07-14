package com.radriver.kannada.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radriver.kannada.R
import com.radriver.kannada.data.local.dao.CategoryProgress
import com.radriver.kannada.domain.model.MASTERY_THRESHOLD

/** Stateful entry point: owns the ViewModel, hoists state into the stateless content. */
@Composable
fun DashboardScreen(
    viewModelFactory: ViewModelProvider.Factory,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardContent(uiState = uiState, modifier = modifier)
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "title") {
            Text(
                text = stringResource(R.string.dashboard_title),
                style = MaterialTheme.typography.headlineMedium
            )
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

        item(key = "category-title") {
            Text(
                text = stringResource(R.string.category_progress_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(uiState.categoryProgress, key = { it.category.name }) { progress ->
            CategoryProgressCard(progress = progress)
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun CategoryProgressCard(
    progress: CategoryProgress,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = progress.category.displayName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(
                        R.string.words_mastered_value,
                        progress.mastered,
                        progress.total
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = {
                    if (progress.total == 0) 0f
                    else progress.mastered.toFloat() / progress.total.toFloat()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
