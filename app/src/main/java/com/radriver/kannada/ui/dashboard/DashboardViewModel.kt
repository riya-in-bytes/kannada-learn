package com.radriver.kannada.ui.dashboard

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radriver.kannada.data.local.dao.CategoryProgress
import com.radriver.kannada.data.repository.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Immutable
data class DashboardUiState(
    val isLoading: Boolean = true,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val masteredWords: Int = 0,
    val totalWords: Int = 0,
    val categoryProgress: List<CategoryProgress> = emptyList()
)

class DashboardViewModel(
    progressRepository: ProgressRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = progressRepository.dashboardData
        .map { data ->
            DashboardUiState(
                isLoading = false,
                currentStreak = data.currentStreak,
                longestStreak = data.longestStreak,
                masteredWords = data.masteredWords,
                totalWords = data.totalWords,
                categoryProgress = data.categoryProgress
            )
        }
        .stateIn(
            scope = viewModelScope,
            // Keep upstream alive across quick tab switches / rotations, stop
            // 5s after the last collector to avoid leaking Room observers.
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )
}
