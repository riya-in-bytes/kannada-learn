package com.radriver.kannada.ui.flashcards

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.data.repository.WordRepository
import com.radriver.kannada.domain.model.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@Immutable
data class FlashcardsUiState(
    val isLoading: Boolean = true,
    /** null = "All" filter. */
    val selectedCategory: Category? = null,
    val words: List<WordEntity> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class FlashcardsViewModel(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val selectedCategory = MutableStateFlow<Category?>(null)

    /**
     * flatMapLatest re-subscribes the Room flow whenever the filter changes,
     * cancelling the previous query's collection.
     */
    private val words = selectedCategory.flatMapLatest { category ->
        if (category == null) wordRepository.getAllWords()
        else wordRepository.getWordsByCategory(category)
    }

    val uiState: StateFlow<FlashcardsUiState> =
        combine(selectedCategory, words) { category, wordList ->
            FlashcardsUiState(
                isLoading = false,
                selectedCategory = category,
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
}
