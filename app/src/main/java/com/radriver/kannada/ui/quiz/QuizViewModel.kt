package com.radriver.kannada.ui.quiz

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radriver.kannada.data.prefs.UserPrefs
import com.radriver.kannada.data.repository.ProgressRepository
import com.radriver.kannada.data.repository.WordRepository
import com.radriver.kannada.domain.model.Level
import com.radriver.kannada.domain.model.QuizMode
import com.radriver.kannada.domain.model.QuizQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
sealed interface QuizUiState {

    /** Pre-quiz: user picks the mode, level and question count. */
    @Immutable
    data class Setup(
        val questionCount: Int = DEFAULT_QUESTION_COUNT,
        val mode: QuizMode = QuizMode.EN_TO_KN,
        /** null = all levels. */
        val level: Level? = null,
        val dueForReview: Int = 0,
        val isBuilding: Boolean = false,
        val notEnoughWords: Boolean = false
    ) : QuizUiState

    @Immutable
    data class InProgress(
        val questions: List<QuizQuestion>,
        val currentIndex: Int,
        /** Word id the user tapped for the current question; null until answered. */
        val selectedWordId: Int?,
        val score: Int,
        val mode: QuizMode,
        val isReview: Boolean = false
    ) : QuizUiState {
        val currentQuestion: QuizQuestion get() = questions[currentIndex]
        val answered: Boolean get() = selectedWordId != null
        val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
    }

    @Immutable
    data class Finished(
        val score: Int,
        val total: Int,
        val isReview: Boolean = false
    ) : QuizUiState

    companion object {
        const val DEFAULT_QUESTION_COUNT = 7
    }
}

/**
 * Quiz state is built imperatively by [startQuiz]/[startReview] and held in a
 * plain MutableStateFlow — deliberately NOT derived from a cold flow with
 * stateIn, which would re-randomize the quiz (and wipe progress) every time
 * the screen's collector unsubscribed for longer than the stateIn timeout.
 * The two collectors in init only ever touch Setup states (guarded update),
 * so they can never disturb a quiz in progress.
 */
class QuizViewModel(
    private val wordRepository: WordRepository,
    private val progressRepository: ProgressRepository,
    private val userPrefs: UserPrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Setup())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // Latest ambient values, cached so onRestart can rebuild a fresh Setup
    // even while the state is InProgress/Finished (the guarded collectors
    // below drop updates that arrive mid-quiz).
    private var latestLevel: Level? = null
    private var latestDueCount = 0
    private var latestMode = QuizMode.EN_TO_KN

    init {
        // Level is shared with the flashcards filter via DataStore; collect
        // continuously so a change made on the other tab shows up here.
        viewModelScope.launch {
            userPrefs.selectedLevel.collect { level ->
                latestLevel = level
                _uiState.update { if (it is QuizUiState.Setup) it.copy(level = level) else it }
            }
        }
        viewModelScope.launch {
            wordRepository.dueForReviewCount().collect { count ->
                latestDueCount = count
                _uiState.update { if (it is QuizUiState.Setup) it.copy(dueForReview = count) else it }
            }
        }
    }

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

    fun onModeSelected(mode: QuizMode) {
        latestMode = mode
        _uiState.update { state ->
            if (state is QuizUiState.Setup) state.copy(mode = mode) else state
        }
    }

    /** Persists to DataStore; the init collector loops the change back into Setup. */
    fun onLevelSelected(level: Level?) {
        viewModelScope.launch { userPrefs.setSelectedLevel(level) }
    }

    fun startQuiz() {
        val setup = _uiState.value as? QuizUiState.Setup ?: return
        if (setup.isBuilding) return

        _uiState.value = setup.copy(isBuilding = true, notEnoughWords = false)
        viewModelScope.launch {
            val questions = wordRepository.buildQuiz(setup.questionCount, setup.level)
            _uiState.value = if (questions.isEmpty()) {
                // Too few words at the selected level (or, on a broken install,
                // an unseeded DB) — surface a message instead of a broken quiz.
                setup.copy(isBuilding = false, notEnoughWords = true)
            } else {
                QuizUiState.InProgress(
                    questions = questions,
                    currentIndex = 0,
                    selectedWordId = null,
                    score = 0,
                    mode = setup.mode
                )
            }
        }
    }

    /** Review ignores the level filter and size slider: everything due, up to the max. */
    fun startReview() {
        val setup = _uiState.value as? QuizUiState.Setup ?: return
        if (setup.isBuilding) return

        _uiState.value = setup.copy(isBuilding = true, notEnoughWords = false)
        viewModelScope.launch {
            val questions = wordRepository.buildReviewQuiz(WordRepository.MAX_QUIZ_SIZE)
            _uiState.value = if (questions.isEmpty()) {
                // Nothing due anymore (e.g. reviewed on another day boundary).
                setup.copy(isBuilding = false, dueForReview = 0)
            } else {
                QuizUiState.InProgress(
                    questions = questions,
                    currentIndex = 0,
                    selectedWordId = null,
                    score = 0,
                    mode = setup.mode,
                    isReview = true
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
        // Fire-and-forget: mastery/review bookkeeping must not block the UI.
        // Always the tested word's id — a wrong answer counts against the
        // word the user failed, not the distractor they happened to tap.
        viewModelScope.launch {
            progressRepository.onAnswer(state.currentQuestion.correctWordId, isCorrect)
        }
    }

    fun onNext() {
        val state = _uiState.value as? QuizUiState.InProgress ?: return
        if (!state.answered) return

        _uiState.value = if (state.isLastQuestion) {
            QuizUiState.Finished(
                score = state.score,
                total = state.questions.size,
                isReview = state.isReview
            )
        } else {
            state.copy(currentIndex = state.currentIndex + 1, selectedWordId = null)
        }
    }

    fun onRestart() {
        _uiState.value = QuizUiState.Setup(
            mode = latestMode,
            level = latestLevel,
            dueForReview = latestDueCount
        )
    }
}
