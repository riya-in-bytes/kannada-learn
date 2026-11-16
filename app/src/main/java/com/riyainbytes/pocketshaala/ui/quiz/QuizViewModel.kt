package com.riyainbytes.pocketshaala.ui.quiz

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riyainbytes.pocketshaala.data.prefs.UserPrefs
import com.riyainbytes.pocketshaala.data.repository.ProgressRepository
import com.riyainbytes.pocketshaala.data.repository.WordRepository
import com.riyainbytes.pocketshaala.data.session.LanguageSession
import com.riyainbytes.pocketshaala.domain.model.Language
import com.riyainbytes.pocketshaala.domain.model.Level
import com.riyainbytes.pocketshaala.domain.model.QuizMode
import com.riyainbytes.pocketshaala.domain.model.QuizQuestion
import com.riyainbytes.pocketshaala.domain.model.QuizWord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** A question answered wrong, with the option the user tapped. */
@Immutable
data class QuizMistake(
    val question: QuizQuestion,
    val pickedWordId: Int
) {
    val picked: QuizWord get() = question.options.first { it.wordId == pickedWordId }
}

@Immutable
sealed interface QuizUiState {

    /** The language this state's words belong to; a switch resets to a fresh [Setup]. */
    val language: Language

    /** Pre-quiz: user picks the mode, level and question count. */
    @Immutable
    data class Setup(
        override val language: Language,
        val questionCount: Int = DEFAULT_QUESTION_COUNT,
        val mode: QuizMode = QuizMode.EN_TO_TARGET,
        /** null = all levels. */
        val level: Level? = null,
        val dueForReview: Int = 0,
        val isBuilding: Boolean = false,
        val notEnoughWords: Boolean = false
    ) : QuizUiState

    @Immutable
    data class InProgress(
        override val language: Language,
        val questions: List<QuizQuestion>,
        val currentIndex: Int,
        /** Word id the user tapped for the current question; null until answered. */
        val selectedWordId: Int?,
        val score: Int,
        val mode: QuizMode,
        val isReview: Boolean = false,
        /** A practice round over the previous session's mistakes; doesn't touch progress. */
        val isRetry: Boolean = false,
        val mistakes: List<QuizMistake> = emptyList()
    ) : QuizUiState {
        val currentQuestion: QuizQuestion get() = questions[currentIndex]
        val answered: Boolean get() = selectedWordId != null
        val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
    }

    @Immutable
    data class Finished(
        override val language: Language,
        val score: Int,
        val total: Int,
        /** Kept so a retry round replays in the same mode. */
        val mode: QuizMode,
        val isReview: Boolean = false,
        val isRetry: Boolean = false,
        val mistakes: List<QuizMistake> = emptyList()
    ) : QuizUiState

    companion object {
        const val DEFAULT_QUESTION_COUNT = 7
    }
}

/**
 * Quiz state is built imperatively by [startQuiz]/[startReview]/[retryMistakes]
 * and held in a plain MutableStateFlow — deliberately NOT derived from a cold
 * flow with stateIn, which would re-randomize the quiz (and wipe progress) every time
 * the screen's collector unsubscribed for longer than the stateIn timeout.
 * The level and due-count collectors in init only ever touch Setup states
 * (guarded update), so they can never disturb a quiz in progress. The one
 * exception is a language switch, which deliberately discards whatever is
 * on screen for a fresh Setup in the new language.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModel(
    private val wordRepository: WordRepository,
    private val progressRepository: ProgressRepository,
    private val userPrefs: UserPrefs,
    private val languageSession: LanguageSession
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<QuizUiState>(QuizUiState.Setup(language = languageSession.current.value))
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // Latest ambient values, cached so onRestart can rebuild a fresh Setup
    // even while the state is InProgress/Finished (the guarded collectors
    // below drop updates that arrive mid-quiz).
    private var latestLevel: Level? = null
    private var latestMode = QuizMode.EN_TO_TARGET

    /** Due count tagged with its language, so a Setup never shows another language's count. */
    private var latestDue: Pair<Language, Int>? = null

    /** The in-flight quiz/review build; cancelled when the language switches under it. */
    private var buildJob: Job? = null

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
            languageSession.current
                .flatMapLatest { language ->
                    wordRepository.dueForReviewCount(language).map { count -> language to count }
                }
                .collect { due ->
                    latestDue = due
                    _uiState.update {
                        if (it is QuizUiState.Setup && it.language == due.first) {
                            it.copy(dueForReview = due.second)
                        } else it
                    }
                }
        }
        // Compared against the state's own language rather than skipping the
        // first emission, so a switch made before this tab was ever opened is
        // still honoured.
        viewModelScope.launch {
            languageSession.current.collect { language ->
                if (_uiState.value.language != language) {
                    buildJob?.cancel()
                    _uiState.value = freshSetup(language)
                }
            }
        }
    }

    private fun freshSetup(language: Language) = QuizUiState.Setup(
        language = language,
        mode = latestMode,
        level = latestLevel,
        dueForReview = latestDue?.takeIf { it.first == language }?.second ?: 0
    )

    /**
     * Lands a finished build only while its "building" Setup is still on
     * screen in the same language. A language switch cancels [buildJob], but
     * a build that has already returned can't be cancelled — this guard stops
     * it from overwriting the new language's Setup.
     */
    private fun publishBuild(language: Language, result: QuizUiState) {
        _uiState.update { current ->
            if (current is QuizUiState.Setup && current.isBuilding && current.language == language) {
                result
            } else current
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
        buildJob = viewModelScope.launch {
            val questions = wordRepository.buildQuiz(setup.language, setup.questionCount, setup.level)
            publishBuild(setup.language, if (questions.isEmpty()) {
                // Too few words at the selected level (or, on a broken install,
                // an unseeded DB) — surface a message instead of a broken quiz.
                setup.copy(isBuilding = false, notEnoughWords = true)
            } else {
                QuizUiState.InProgress(
                    language = setup.language,
                    questions = questions,
                    currentIndex = 0,
                    selectedWordId = null,
                    score = 0,
                    mode = setup.mode
                )
            })
        }
    }

    /** Review ignores the level filter and size slider: everything due, up to the max. */
    fun startReview() {
        val setup = _uiState.value as? QuizUiState.Setup ?: return
        if (setup.isBuilding) return

        _uiState.value = setup.copy(isBuilding = true, notEnoughWords = false)
        buildJob = viewModelScope.launch {
            val questions = wordRepository.buildReviewQuiz(setup.language, WordRepository.MAX_QUIZ_SIZE)
            publishBuild(setup.language, if (questions.isEmpty()) {
                // Nothing due anymore (e.g. reviewed on another day boundary).
                setup.copy(isBuilding = false, dueForReview = 0)
            } else {
                QuizUiState.InProgress(
                    language = setup.language,
                    questions = questions,
                    currentIndex = 0,
                    selectedWordId = null,
                    score = 0,
                    mode = setup.mode,
                    isReview = true
                )
            })
        }
    }

    fun onOptionSelected(wordId: Int) {
        val state = _uiState.value as? QuizUiState.InProgress ?: return
        if (state.answered) return

        val isCorrect = wordId == state.currentQuestion.correctWordId
        _uiState.value = state.copy(
            selectedWordId = wordId,
            score = if (isCorrect) state.score + 1 else state.score,
            mistakes = if (isCorrect) state.mistakes
            else state.mistakes + QuizMistake(state.currentQuestion, wordId)
        )
        // A retry is practice right after seeing the answers — recording it
        // would let words reach MASTERY_THRESHOLD (and dodge review) too easily.
        if (state.isRetry) return
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
                language = state.language,
                score = state.score,
                total = state.questions.size,
                mode = state.mode,
                isReview = state.isReview,
                isRetry = state.isRetry,
                mistakes = state.mistakes
            )
        } else {
            state.copy(currentIndex = state.currentIndex + 1, selectedWordId = null)
        }
    }

    /** Re-asks only the missed questions, reshuffled so positions can't be memorized. */
    fun retryMistakes() {
        val finished = _uiState.value as? QuizUiState.Finished ?: return
        if (finished.mistakes.isEmpty()) return

        _uiState.value = QuizUiState.InProgress(
            language = finished.language,
            questions = finished.mistakes
                .map { it.question.copy(options = it.question.options.shuffled()) }
                .shuffled(),
            currentIndex = 0,
            selectedWordId = null,
            score = 0,
            mode = finished.mode,
            isRetry = true
        )
    }

    fun onRestart() {
        _uiState.value = freshSetup(languageSession.current.value)
    }
}
