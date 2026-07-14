package com.radriver.kannada.ui.flashcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.radriver.kannada.R
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.ui.components.FlashCard
import com.radriver.kannada.ui.components.TtsUnavailableHint
import com.radriver.kannada.util.TtsManager

/** Stateful entry point: owns the ViewModel, hoists state into the stateless content. */
@Composable
fun FlashcardsScreen(
    viewModelFactory: ViewModelProvider.Factory,
    ttsManager: TtsManager,
    modifier: Modifier = Modifier,
    viewModel: FlashcardsViewModel = viewModel(factory = viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FlashcardsContent(
        uiState = uiState,
        ttsManager = ttsManager,
        onCategorySelected = viewModel::onCategorySelected,
        modifier = modifier
    )
}

@Composable
private fun FlashcardsContent(
    uiState: FlashcardsUiState,
    ttsManager: TtsManager,
    onCategorySelected: (Category?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.flashcards_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )

        CategoryFilterRow(
            selected = uiState.selectedCategory,
            onCategorySelected = onCategorySelected
        )

        TtsUnavailableHint(
            ttsManager = ttsManager,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stable keys: moves/updates animate correctly and item state
            // (e.g. speaker ripple) never bleeds between cards.
            items(uiState.words, key = { it.id }) { word ->
                FlashCard(word = word, ttsManager = ttsManager)
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selected: Category?,
    onCategorySelected: (Category?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onCategorySelected(null) },
            label = { Text(stringResource(R.string.category_all)) }
        )
        Category.entries.forEach { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName) }
            )
        }
    }
}
