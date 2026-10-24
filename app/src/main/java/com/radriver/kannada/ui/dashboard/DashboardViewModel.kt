package com.radriver.kannada.ui.dashboard

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radriver.kannada.data.local.dao.CategoryProgress
import com.radriver.kannada.data.local.dao.LevelProgress
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.data.prefs.UserPrefs
import com.radriver.kannada.data.repository.ProgressRepository
import com.radriver.kannada.data.repository.WordRepository
import com.radriver.kannada.domain.model.Category
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@Immutable
data class DashboardUiState(
    val isLoading: Boolean = true,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val masteredWords: Int = 0,
    val totalWords: Int = 0,
    val categoryProgress: List<CategoryProgress> = emptyList(),
    val levelProgress: List<LevelProgress> = emptyList(),
    val dueForReview: Int = 0,
    val wordOfTheDay: WordEntity? = null
)

class DashboardViewModel(
    progressRepository: ProgressRepository,
    wordRepository: WordRepository,
    private val userPrefs: UserPrefs
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        progressRepository.dashboardData,
        wordRepository.wordOfTheDay(),
        userPrefs.wotdDismissedEpochDay
    ) { data, wordOfTheDay, dismissedEpochDay ->
        // Hide today's word once dismissed; it returns tomorrow because
        // wordOfTheDay() picks a new word each day and "today" is read here
        // per emission (correct across a midnight rollover).
        val visibleWord =
            wordOfTheDay?.takeUnless { dismissedEpochDay == LocalDate.now().toEpochDay() }
        DashboardUiState(
            isLoading = false,
            currentStreak = data.currentStreak,
            longestStreak = data.longestStreak,
            masteredWords = data.masteredWords,
            totalWords = data.totalWords,
            categoryProgress = data.categoryProgress,
            levelProgress = data.levelProgress,
            dueForReview = data.dueForReview,
            wordOfTheDay = visibleWord
        )
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

    /** Dismisses the Word of the Day for the rest of today. */
    fun onDismissWordOfTheDay() {
        viewModelScope.launch { userPrefs.dismissWordOfTheDay() }
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
