package com.radriver.kannada.domain.model

/**
 * Difficulty level of a word. Stored in Room as the enum name via a
 * TypeConverter, so entries are append-only: renaming or removing one is a
 * schema-affecting change. Levels are freely selectable (no unlocking), so
 * no ordering/progression logic belongs here.
 */
enum class Level(val displayName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}
