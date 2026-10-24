package com.radriver.kannada.domain.model

/**
 * Vocabulary categories. Stored in Room as the enum name via a TypeConverter,
 * so entries are append-only: renaming or removing one is a schema-affecting
 * change, and a DB that has seen a newer APK may contain names an older APK
 * can't parse (downgrades are unsupported).
 */
enum class Category(val displayName: String) {
    GREETINGS("Greetings"),
    VERBS("Essential Verbs"),
    CONVERSATIONS("Conversations"),
    FAMILY("Family"),
    FOOD("Food & Eating"),
    TIME_DAYS("Time & Days"),
    PLACES("Places"),
    // NUMBERS is intentionally last so it sits at the end of the flashcards
    // category chip row (which iterates Category.entries). Safe to reorder:
    // the TypeConverter stores the enum name, not its ordinal.
    NUMBERS("Numbers")
}
