package com.riyainbytes.pocketshaala.data.local

import androidx.room.TypeConverter
import com.riyainbytes.pocketshaala.domain.model.Category
import com.riyainbytes.pocketshaala.domain.model.Language
import com.riyainbytes.pocketshaala.domain.model.Level

class Converters {
    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(name: String): Category = Category.valueOf(name)

    @TypeConverter
    fun fromLevel(level: Level): String = level.name

    @TypeConverter
    fun toLevel(name: String): Level = Level.valueOf(name)

    @TypeConverter
    fun fromLanguage(language: Language): String = language.name

    @TypeConverter
    fun toLanguage(name: String): Language = Language.valueOf(name)
}
