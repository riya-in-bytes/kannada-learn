package com.radriver.kannada.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.radriver.kannada.R
import com.radriver.kannada.domain.model.Level

/**
 * Level filter chips: "All" (null) + one chip per [Level]. Shared by the
 * flashcards filter bar and the quiz setup screen.
 */
@Composable
fun LevelFilterRow(
    selected: Level?,
    onLevelSelected: (Level?) -> Unit,
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
            onClick = { onLevelSelected(null) },
            label = { Text(stringResource(R.string.level_all)) }
        )
        Level.entries.forEach { level ->
            FilterChip(
                selected = selected == level,
                onClick = { onLevelSelected(level) },
                label = { Text(level.displayName) }
            )
        }
    }
}
