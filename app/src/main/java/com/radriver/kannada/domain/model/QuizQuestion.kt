package com.radriver.kannada.domain.model

/**
 * One multiple-choice option: a Kannada rendering of some word.
 * Carries transliteration so the quiz UI can show it under the script,
 * and the raw Kannada text so the speaker button can voice it.
 */
data class QuizOption(
    val wordId: Int,
    val kannada: String,
    val transliteration: String
)

/**
 * A single quiz question: "Which is the Kannada word for [promptEnglish]?"
 * [options] always contains the correct answer plus unique distractors,
 * pre-shuffled at build time so the UI renders it as-is.
 */
data class QuizQuestion(
    val correctWordId: Int,
    val promptEnglish: String,
    val options: List<QuizOption>
)
