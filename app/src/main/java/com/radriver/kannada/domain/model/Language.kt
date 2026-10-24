package com.radriver.kannada.domain.model

import java.util.Locale

/**
 * A language the app teaches. Stored in Room as the enum name via a
 * TypeConverter, so entries are append-only (same rule as [Category] and
 * [Level]).
 *
 * [showsScript] is the rendering rule: when false the native script is kept
 * in the database only so the TTS voice can pronounce it, and the UI shows
 * the transliteration in its place — the script must never reach the screen.
 * [ttsLocales] are tried in order (regional first, then bare language).
 */
enum class Language(
    val displayName: String,
    /** The language's own name in its script, or null when [showsScript] is false. */
    val nativeName: String?,
    /** Short marker for the quiz-mode labels ("EN → ಕ" / "EN → GU"). */
    val quizBadge: String,
    val showsScript: Boolean,
    val ttsLocales: List<Locale>
) {
    KANNADA(
        displayName = "Kannada",
        nativeName = "ಕನ್ನಡ",
        quizBadge = "ಕ",
        showsScript = true,
        ttsLocales = listOf(Locale("kn", "IN"), Locale("kn"))
    ),
    GUJARATI(
        displayName = "Gujarati",
        nativeName = null,
        quizBadge = "GU",
        showsScript = false,
        ttsLocales = listOf(Locale("gu", "IN"), Locale("gu"))
    );

    /**
     * Salt for date/seed-based picks (word of the day, flashcard shuffle) so
     * mirrored vocabularies don't surface the same English word in lockstep.
     * Zero for KANNADA, which keeps its original picks.
     */
    val variationSalt: Int get() = ordinal * 7919

    companion object {
        /** Every fresh launch starts here; the active language is never persisted. */
        val DEFAULT = KANNADA
    }
}

/**
 * The text a learner sees for a word: its script, or — for a language that
 * hides its script — the transliteration in its place.
 */
fun Language.displayText(script: String, transliteration: String): String =
    if (showsScript) script else transliteration

/**
 * Case- and punctuation-insensitive form of a transliteration. For a language
 * that doesn't show its script this is the only visible text, so seed checks
 * and quiz option dedupe compare on it.
 */
fun romanizationKey(transliteration: String): String =
    transliteration.lowercase().filter(Char::isLetterOrDigit)
