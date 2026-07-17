package com.radriver.kannada.domain.model

/**
 * How a quiz session renders its questions. One mode applies to the whole
 * session (chosen on the setup screen); the question data itself is
 * mode-agnostic because [QuizWord] carries every surface of a word.
 */
enum class QuizMode {
    /** Prompt is English, options are Kannada (the original mode). */
    EN_TO_KN,

    /** Prompt is Kannada script + transliteration, options are English. */
    KN_TO_EN,

    /** Prompt is TTS audio only (script hidden until answered), options are English. */
    LISTENING
}

/**
 * One word rendered as a prompt or an option. Carries all three surfaces
 * (English, Kannada script, transliteration) so the UI picks what to show
 * per [QuizMode] and the speaker button can voice the Kannada text.
 */
data class QuizWord(
    val wordId: Int,
    val english: String,
    val kannada: String,
    val transliteration: String
)

/**
 * A single quiz question. [prompt] is the correct word itself; [options]
 * always contains it plus distractors unique by both Kannada and English
 * text, pre-shuffled at build time so the UI renders the list as-is.
 */
data class QuizQuestion(
    val correctWordId: Int,
    val prompt: QuizWord,
    val options: List<QuizWord>
)
