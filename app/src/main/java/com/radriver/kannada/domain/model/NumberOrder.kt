package com.radriver.kannada.domain.model

/**
 * Display order for the [Category.NUMBERS] flashcards, matched by
 * [com.radriver.kannada.data.local.entity.WordEntity.english]. The main run
 * counts up "serially" (1-10, the tens, the hundreds, 1000); the remaining
 * numbers (0, 11-19, 21-29) follow it in numeric order. A number word missing
 * from this list sorts after all of them, so new seed numbers still show up —
 * add them here to place them.
 */
private val NUMBER_DISPLAY_ORDER = listOf(
    // Main run
    "One", "Two", "Three", "Four", "Five",
    "Six", "Seven", "Eight", "Nine", "Ten",
    "Twenty", "Thirty", "Forty", "Fifty",
    "Sixty", "Seventy", "Eighty", "Ninety",
    "Hundred", "Two hundred", "Three hundred", "Four hundred", "Five hundred",
    "Six hundred", "Seven hundred", "Eight hundred", "Nine hundred",
    "Thousand",
    // The rest
    "Zero",
    "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
    "Sixteen", "Seventeen", "Eighteen", "Nineteen",
    "Twenty-one", "Twenty-two", "Twenty-three", "Twenty-four", "Twenty-five",
    "Twenty-six", "Twenty-seven", "Twenty-eight", "Twenty-nine"
).withIndex().associate { (index, english) -> english to index }

/** Sort key for a number word's English text; unknown words sort last. */
fun numberDisplayRank(english: String): Int = NUMBER_DISPLAY_ORDER[english] ?: Int.MAX_VALUE
