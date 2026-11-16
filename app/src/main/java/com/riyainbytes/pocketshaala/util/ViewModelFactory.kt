package com.riyainbytes.pocketshaala.util

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.riyainbytes.pocketshaala.AppContainer
import com.riyainbytes.pocketshaala.ui.dashboard.DashboardViewModel
import com.riyainbytes.pocketshaala.ui.flashcards.FlashcardsViewModel
import com.riyainbytes.pocketshaala.ui.languages.LanguagesViewModel
import com.riyainbytes.pocketshaala.ui.mastered.MasteredWordsViewModel
import com.riyainbytes.pocketshaala.ui.quiz.QuizViewModel

/**
 * Single factory for every ViewModel in the app, built with the modern
 * [viewModelFactory] DSL (CreationExtras-capable, so a SavedStateHandle can
 * be added to any initializer later without restructuring).
 */
fun appViewModelFactory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        DashboardViewModel(
            container.progressRepository,
            container.wordRepository,
            container.userPrefs,
            container.languageSession
        )
    }
    initializer { FlashcardsViewModel(container.wordRepository, container.userPrefs, container.languageSession) }
    initializer { MasteredWordsViewModel(container.wordRepository, container.languageSession) }
    initializer {
        QuizViewModel(
            container.wordRepository,
            container.progressRepository,
            container.userPrefs,
            container.languageSession
        )
    }
    initializer { LanguagesViewModel(container.progressRepository, container.languageSession) }
}
