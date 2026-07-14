package com.radriver.kannada.ui.quiz

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radriver.kannada.data.repository.ProgressRepository
import com.radriver.kannada.data.repository.WordRepository
import com.radriver.kannada.domain.model.QuizQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
sealed interface QuizUiState {

    /** Pre-quiz: user picks the question count. */
    @Immutable
    data class Setup(
        val questionCount: Int = DEFAULT_QUESTION_COUNT,
        val isBuilding: Boolean = false,
        val notEnoughWords: Boolean = false
    ) : QuizUiState

    @Immutable
    data class InProgress(
        val questions: List<QuizQuestion>,
        val currentIndex: Int,
        /** Word id the user tapped for the current question; null until answered. */
        val selectedWordId: Int?,
        val score: Int
    ) : QuizUiState {
        val currentQuestion: QuizQuestion get() = questions[currentIndex]
        val answered: Boolean get() = selectedWordId != null
        val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
    }

    @Immutable
    data class Finished(val score: Int, val total: Int) : QuizUiState

    companion object {
        const val DEFAULT_QUESTION_COUNT = 7
    }
}

/**
 * Quiz state is built imperatively by [startQuiz] and held in a plain
 * MutableStateFlow — deliberately NOT derived from a cold flow with stateIn,
 * which would re-randomize the quiz (and wipe progress) every time the
 * screen's collector unsubscribed for longer than the stateIn timeout.
 */
class QuizViewModel(
    private val wordRepository: WordRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Setup())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun onQuestionCountChanged(count: Int) {
        _uiState.update { state ->
            if (state is QuizUiState.Setup) {
                state.copy(
                    questionCount = count.coerceIn(
                        WordRepository.MIN_QUIZ_SIZE,
                        WordRepository.MAX_QUIZ_SIZE
                    )
                )
            } else state
        }
    }

    fun startQuiz() {
        val setup = _uiState.value as? QuizUiState.Setup ?: return
        if (setup.isBuilding) return

        _uiState.value = setup.copy(isBuilding = true, notEnoughWords = false)
        viewModelScope.launch {
            val questions = wordRepository.buildQuiz(setup.questionCount)
            _uiState.value = if (questions.isEmpty()) {
                // Only possible before first seed completes (or an empty DB) —
                // surface a loading/empty message instead of a broken quiz.
                setup.copy(isBuilding = false, notEnoughWords = true)
            } else {
                QuizUiState.InProgress(
                    questions = questions,
                    currentIndex = 0,
                    selectedWordId = null,
                    score = 0
                )
            }
        }
    }

    fun onOptionSelected(wordId: Int) {
        val state = _uiState.value as? QuizUiState.InProgress ?: return
        if (state.answered) return

        val isCorrect = wordId == state.currentQuestion.correctWordId
        _uiState.value = state.copy(
            selectedWordId = wordId,
            score = if (isCorrect) state.score + 1 else state.score
        )
        if (isCorrect) {
            // Fire-and-forget: mastery bookkeeping must not block the UI.
            viewModelScope.launch {
                progressRepository.onCorrectAnswer(wordId)
            }
        }
    }

    fun onNext() {
        val state = _uiState.value as? QuizUiState.InProgress ?: return
        if (!state.answered) return

        _uiState.value = if (state.isLastQuestion) {
            QuizUiState.Finished(score = state.score, total = state.questions.size)
        } else {
            state.copy(currentIndex = state.currentIndex + 1, selectedWordId = null)
        }
    }

    fun onRestart() {
        _uiState.value = QuizUiState.Setup()
    }
}
