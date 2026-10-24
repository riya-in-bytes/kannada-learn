package com.radriver.kannada.ui.flashcards

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.data.prefs.UserPrefs
import com.radriver.kannada.data.repository.WordRepository
import com.radriver.kannada.data.session.LanguageSession
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.Language
import com.radriver.kannada.domain.model.Level
import com.radriver.kannada.domain.model.numberDisplayRank
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

@Immutable
data class FlashcardsUiState(
    val isLoading: Boolean = true,
    /** The language [words] belong to (drives the title badge). */
    val language: Language = Language.DEFAULT,
    /** null = "All" filter. */
    val selectedCategory: Category? = null,
    /** null = "All" filter. */
    val selectedLevel: Level? = null,
    val words: List<WordEntity> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class FlashcardsViewModel(
    private val wordRepository: WordRepository,
    private val userPrefs: UserPrefs,
    languageSession: LanguageSession
) : ViewModel() {

    /**
     * Both filters live in DataStore rather than in local MutableStateFlows:
     * each is written from more than one screen — level by the quiz setup,
     * category by the dashboard's "Progress by Category" rows, which set the
     * filter and then switch to this tab — so the source of truth has to sit
     * below every ViewModel that touches it. It also survives process death.
     */
    private val selectedCategory = userPrefs.selectedCategory
    private val selectedLevel = userPrefs.selectedLevel

    /** Advances every few app launches; drives the deterministic reshuffle. */
    private val shuffleSeed = userPrefs.shuffleSeed

    private data class Query(val language: Language, val level: Level?, val category: Category?, val seed: Long)

    /**
     * flatMapLatest re-subscribes the Room flow whenever the language, a
     * filter or the shuffle seed changes, cancelling the previous query's
     * collection. Each emission is tagged with its language. The
     * DAO returns id-ordered rows; we reorder them here so the same words
     * don't always sit at the top. Mixing the active filter into the seed
     * makes each category/level view shuffle to its own stable order (the
     * language salt keeps mirrored vocabularies from shuffling in lockstep),
     * and the seed only advances on a new launch — so a session never reshuffles
     * mid-use (rotation, tab switch). The Numbers tab is the exception: it is
     * never shuffled and counts up in [numberDisplayRank] order instead.
     */
    private val words = combine(
        languageSession.current, selectedLevel, selectedCategory, shuffleSeed, ::Query
    )
        .flatMapLatest { (language, level, category, seed) ->
            wordRepository.getWords(language, level, category).map { list ->
                language to if (category == Category.NUMBERS) {
                    // Stable sort: unranked numbers keep their id order.
                    list.sortedBy { numberDisplayRank(it.english) }
                } else {
                    val mixed = seed * 31 + (category?.ordinal ?: 0) * 7 + (level?.ordinal ?: 0) +
                        language.variationSalt
                    list.shuffled(Random(mixed))
                }
            }
        }

    val uiState: StateFlow<FlashcardsUiState> =
        combine(selectedCategory, selectedLevel, words) { category, level, (language, wordList) ->
            FlashcardsUiState(
                isLoading = false,
                language = language,
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
        viewModelScope.launch { userPrefs.setSelectedCategory(category) }
    }

    fun onLevelSelected(level: Level?) {
        viewModelScope.launch { userPrefs.setSelectedLevel(level) }
    }
}
