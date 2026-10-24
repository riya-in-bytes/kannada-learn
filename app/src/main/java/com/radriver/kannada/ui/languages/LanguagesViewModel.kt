package com.radriver.kannada.ui.languages

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radriver.kannada.data.repository.LanguageSummary
import com.radriver.kannada.data.repository.ProgressRepository
import com.radriver.kannada.data.session.LanguageSession
import com.radriver.kannada.domain.model.Language
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Immutable
data class LanguagesUiState(
    val isLoading: Boolean = true,
    val active: Language = Language.DEFAULT,
    val summaries: List<LanguageSummary> = emptyList()
)

class LanguagesViewModel(
    progressRepository: ProgressRepository,
    private val languageSession: LanguageSession
) : ViewModel() {

    val uiState: StateFlow<LanguagesUiState> = combine(
        progressRepository.languageSummaries,
        languageSession.current
    ) { summaries, active ->
        LanguagesUiState(isLoading = false, active = active, summaries = summaries)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LanguagesUiState()
    )

    /** Every tab's ViewModel observes the session, so this one call retargets the app. */
    fun switchTo(language: Language) = languageSession.switchTo(language)
}
