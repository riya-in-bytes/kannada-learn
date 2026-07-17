package com.radriver.kannada.ui.flashcards

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.data.prefs.UserPrefs
import com.radriver.kannada.data.repository.WordRepository
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.Level
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
data class FlashcardsUiState(
    val isLoading: Boolean = true,
    /** null = "All" filter. */
    val selectedCategory: Category? = null,
    /** null = "All" filter. */
    val selectedLevel: Level? = null,
    val words: List<WordEntity> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class FlashcardsViewModel(
    private val wordRepository: WordRepository,
    private val userPrefs: UserPrefs
) : ViewModel() {

    private val selectedCategory = MutableStateFlow<Category?>(null)

    /**
     * Level lives in DataStore (not a local MutableStateFlow like category):
     * the same selection drives the quiz setup screen and survives process
     * death, so the source of truth must sit below both ViewModels.
     */
    private val selectedLevel = userPrefs.selectedLevel

    /**
     * flatMapLatest re-subscribes the Room flow whenever a filter changes,
     * cancelling the previous query's collection.
     */
    private val words = combine(selectedLevel, selectedCategory, ::Pair)
        .flatMapLatest { (level, category) -> wordRepository.getWords(level, category) }

    val uiState: StateFlow<FlashcardsUiState> =
        combine(selectedCategory, selectedLevel, words) { category, level, wordList ->
            FlashcardsUiState(
                isLoading = false,
                selectedCategory = category,
                selectedLevel = level,
                words = wordList
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FlashcardsUiState()
        )

    fun onCategorySelected(category: Category?) {
        selectedCategory.value = category
    }

    fun onLevelSelected(level: Level?) {
        viewModelScope.launch { userPrefs.setSelectedLevel(level) }
    }
}
