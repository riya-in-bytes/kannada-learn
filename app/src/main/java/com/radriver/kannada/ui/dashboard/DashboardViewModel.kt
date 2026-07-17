package com.radriver.kannada.ui.dashboard

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radriver.kannada.data.local.dao.CategoryProgress
import com.radriver.kannada.data.local.dao.LevelProgress
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.data.repository.ProgressRepository
import com.radriver.kannada.data.repository.WordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

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
    wordRepository: WordRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        progressRepository.dashboardData,
        wordRepository.wordOfTheDay()
    ) { data, wordOfTheDay ->
        DashboardUiState(
            isLoading = false,
            currentStreak = data.currentStreak,
            longestStreak = data.longestStreak,
            masteredWords = data.masteredWords,
            totalWords = data.totalWords,
            categoryProgress = data.categoryProgress,
            levelProgress = data.levelProgress,
            dueForReview = data.dueForReview,
            wordOfTheDay = wordOfTheDay
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
}
