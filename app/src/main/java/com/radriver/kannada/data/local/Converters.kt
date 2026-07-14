package com.radriver.kannada.data.local

import androidx.room.TypeConverter
import com.radriver.kannada.domain.model.Category

class Converters {
    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(name: String): Category = Category.valueOf(name)
}
