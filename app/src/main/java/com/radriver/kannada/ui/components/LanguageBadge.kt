package com.radriver.kannada.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.radriver.kannada.R
import com.radriver.kannada.domain.model.Language

/**
 * Small chip naming the language being learned, shown beside each screen's
 * title so it's always clear which vocabulary is on screen. Tapping it opens
 * the Languages tab.
 */
@Composable
fun LanguageBadge(
    language: Language,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = stringResource(R.string.language_badge_description, language.displayName)
    AssistChip(
        onClick = onClick,
        label = { Text(language.displayName) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Translate,
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        border = null,
        modifier = modifier.semantics { contentDescription = description }
    )
}
