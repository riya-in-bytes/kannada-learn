package com.riyainbytes.pocketshaala.ui.mastered

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riyainbytes.pocketshaala.data.local.entity.WordEntity
import com.riyainbytes.pocketshaala.data.repository.WordRepository
import com.riyainbytes.pocketshaala.data.session.LanguageSession
import com.riyainbytes.pocketshaala.domain.model.Category
import com.riyainbytes.pocketshaala.domain.model.Language
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
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
    val language: Language = Language.DEFAULT,
    val total: Int = 0,
    val groups: List<MasteredGroup> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class MasteredWordsViewModel(
    wordRepository: WordRepository,
    languageSession: LanguageSession
) : ViewModel() {

    val uiState: StateFlow<MasteredWordsUiState> = languageSession.current
        .flatMapLatest { language ->
            wordRepository.masteredWords(language).map { words ->
                MasteredWordsUiState(
                    isLoading = false,
                    language = language,
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
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MasteredWordsUiState()
        )
}
