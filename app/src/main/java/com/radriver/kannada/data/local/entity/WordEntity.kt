package com.radriver.kannada.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.MASTERY_THRESHOLD

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val english: String,
    val kannada: String,
    val transliteration: String,
    val category: Category,
    @ColumnInfo(defaultValue = "0")
    val correctCount: Int = 0
) {
    /** Derived, never stored: mastery is a function of [correctCount]. */
    val isMastered: Boolean
        get() = correctCount >= MASTERY_THRESHOLD
}
