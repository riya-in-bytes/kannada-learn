package com.radriver.kannada.util

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.radriver.kannada.AppContainer
import com.radriver.kannada.ui.dashboard.DashboardViewModel
import com.radriver.kannada.ui.flashcards.FlashcardsViewModel
import com.radriver.kannada.ui.mastered.MasteredWordsViewModel
import com.radriver.kannada.ui.quiz.QuizViewModel

/**
 * Single factory for every ViewModel in the app, built with the modern
 * [viewModelFactory] DSL (CreationExtras-capable, so a SavedStateHandle can
 * be added to any initializer later without restructuring).
 */
fun appViewModelFactory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer { DashboardViewModel(container.progressRepository, container.wordRepository, container.userPrefs) }
    initializer { FlashcardsViewModel(container.wordRepository, container.userPrefs) }
    initializer { MasteredWordsViewModel(container.wordRepository) }
    initializer { QuizViewModel(container.wordRepository, container.progressRepository, container.userPrefs) }
}
