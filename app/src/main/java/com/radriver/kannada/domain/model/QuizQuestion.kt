package com.radriver.kannada.domain.model

/**
 * How a quiz session renders its questions. One mode applies to the whole
 * session (chosen on the setup screen); the question data itself is
 * mode-agnostic because [QuizWord] carries every surface of a word.
 */
enum class QuizMode {
    /** Prompt is English, options are in the target language (the original mode). */
    EN_TO_TARGET,

    /** Prompt is the target-language word, options are English. */
    TARGET_TO_EN,

    /** Prompt is TTS audio only (word hidden until answered), options are English. */
    LISTENING
}

/**
 * One word rendered as a prompt or an option. Carries every surface
 * (English, native script, transliteration) so the UI picks what to show
 * per [QuizMode] and the speaker button can voice the [script]. Rendering
 * follows the word's own [language] (see [Language.showsScript]).
 */
data class QuizWord(
    val wordId: Int,
    val english: String,
    val script: String,
    val transliteration: String,
    val language: Language
)

/**
 * A single quiz question. [prompt] is the correct word itself; [options]
 * always contains it plus distractors unique by script, transliteration and
 * English text, pre-shuffled at build time so the UI renders the list as-is.
 */
data class QuizQuestion(
    val correctWordId: Int,
    val prompt: QuizWord,
    val options: List<QuizWord>
)
