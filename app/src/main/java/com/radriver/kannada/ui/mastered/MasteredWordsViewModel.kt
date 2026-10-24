package com.radriver.kannada.ui.mastered

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.data.repository.WordRepository
import com.radriver.kannada.domain.model.Category
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** One category's worth of mastered words, rendered under a header. */
@Immutable
data class MasteredGroup(
    val category: Category,
    val words: List<WordEntity>
)

@Immutable
data class MasteredWordsUiState(
    val isLoading: Boolean = true,
    val total: Int = 0,
    val groups: List<MasteredGroup> = emptyList()
)

class MasteredWordsViewModel(
    wordRepository: WordRepository
) : ViewModel() {

    val uiState: StateFlow<MasteredWordsUiState> = wordRepository.masteredWords()
        .map { words ->
            MasteredWordsUiState(
                isLoading = false,
                total = words.size,
                // Grouped and ordered in Kotlin, not SQL: the category column
                // holds the enum name, so ORDER BY would sort alphabetically
                // instead of in the dashboard's order (Numbers last).
                groups = words
                    .groupBy { it.category }
                    .map { (category, categoryWords) -> MasteredGroup(category, categoryWords) }
                    .sortedBy { it.category.ordinal }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MasteredWordsUiState()
        )
}
