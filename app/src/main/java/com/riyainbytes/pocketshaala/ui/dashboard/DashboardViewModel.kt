package com.riyainbytes.pocketshaala.ui.dashboard

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riyainbytes.pocketshaala.data.local.dao.CategoryProgress
import com.riyainbytes.pocketshaala.data.local.dao.LevelProgress
import com.riyainbytes.pocketshaala.data.local.entity.WordEntity
import com.riyainbytes.pocketshaala.data.prefs.UserPrefs
import com.riyainbytes.pocketshaala.data.repository.DashboardData
import com.riyainbytes.pocketshaala.data.repository.ProgressRepository
import com.riyainbytes.pocketshaala.data.repository.WordRepository
import com.riyainbytes.pocketshaala.data.session.LanguageSession
import com.riyainbytes.pocketshaala.domain.model.Category
import com.riyainbytes.pocketshaala.domain.model.Language
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@Immutable
data class DashboardUiState(
    val isLoading: Boolean = true,
    /** The language every count and the word of the day belong to; the streak is shared. */
    val language: Language = Language.DEFAULT,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val masteredWords: Int = 0,
    val totalWords: Int = 0,
    val categoryProgress: List<CategoryProgress> = emptyList(),
    val levelProgress: List<LevelProgress> = emptyList(),
    val dueForReview: Int = 0,
    val wordOfTheDay: WordEntity? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    progressRepository: ProgressRepository,
    wordRepository: WordRepository,
    private val userPrefs: UserPrefs,
    languageSession: LanguageSession
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = languageSession.current.flatMapLatest { language ->
        combine(
            progressRepository.dashboardData(language),
            wordRepository.wordOfTheDay(language),
            userPrefs.wotdDismissedEpochDay(language)
        ) { data, wordOfTheDay, dismissedEpochDay ->
            // Hide today's word once dismissed; it returns tomorrow because
            // wordOfTheDay() picks a new word each day and "today" is read here
            // per emission (correct across a midnight rollover).
            val visibleWord =
                wordOfTheDay?.takeUnless { dismissedEpochDay == LocalDate.now().toEpochDay() }
            dashboardUiState(language, data, visibleWord)
        }
    }
        .stateIn(
            scope = viewModelScope,
            // Keep upstream alive across quick tab switches / rotations, stop
            // 5s after the last collector to avoid leaking Room observers.
            // Re-subscribing after a tab-away also refreshes the date-based
            // values (word of the day, due-for-review count).
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )

    private fun dashboardUiState(language: Language, data: DashboardData, visibleWord: WordEntity?) =
        DashboardUiState(
            isLoading = false,
            language = language,
            currentStreak = data.currentStreak,
            longestStreak = data.longestStreak,
            masteredWords = data.masteredWords,
            totalWords = data.totalWords,
            categoryProgress = data.categoryProgress,
            levelProgress = data.levelProgress,
            dueForReview = data.dueForReview,
            wordOfTheDay = visibleWord
        )

    /**
     * Dismisses the displayed Word of the Day for the rest of today — keyed
     * by that word's own language, so a dismiss racing a language switch
     * can't hide the other language's word.
     */
    fun onDismissWordOfTheDay() {
        val word = uiState.value.wordOfTheDay ?: return
        viewModelScope.launch { userPrefs.dismissWordOfTheDay(word.language) }
    }

    /**
     * Arms the shared flashcards filter so the caller can switch to that tab
     * and land on the tapped category. The filter lives in DataStore precisely
     * so it can be written from here — see [UserPrefs.selectedCategory].
     */
    fun onCategorySelected(category: Category) {
        viewModelScope.launch { userPrefs.setSelectedCategory(category) }
    }
}
