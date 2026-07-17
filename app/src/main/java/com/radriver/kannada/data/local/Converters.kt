package com.radriver.kannada.data.local

import androidx.room.TypeConverter
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.Level

class Converters {
    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(name: String): Category = Category.valueOf(name)

    @TypeConverter
    fun fromLevel(level: Level): String = level.name

    @TypeConverter
    fun toLevel(name: String): Level = Level.valueOf(name)
}
