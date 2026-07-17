package com.radriver.kannada.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.Level
import com.radriver.kannada.domain.model.MASTERY_THRESHOLD

/**
 * The defaultValue annotations must stay byte-identical to the DEFAULT
 * clauses in [com.radriver.kannada.data.local.AppDatabase]'s migrations —
 * Room validates the live table schema against them on open.
 */
@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val english: String,
    val kannada: String,
    val transliteration: String,
    val category: Category,
    @ColumnInfo(defaultValue = "BEGINNER")
    val level: Level = Level.BEGINNER,
    @ColumnInfo(defaultValue = "0")
    val correctCount: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val wrongCount: Int = 0,
    /** Epoch day of the last quiz answer; null = never answered (not in the review queue). */
    val lastAnsweredEpochDay: Long? = null
) {
    /** Derived, never stored: mastery is a function of [correctCount]. */
    val isMastered: Boolean
        get() = correctCount >= MASTERY_THRESHOLD
}
