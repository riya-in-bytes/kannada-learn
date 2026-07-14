package com.radriver.kannada.domain.model

/**
 * Vocabulary categories. Stored in Room as the enum name via a TypeConverter,
 * so renaming an entry is a schema-affecting change.
 */
enum class Category(val displayName: String) {
    GREETINGS("Greetings"),
    NUMBERS("Numbers"),
    VERBS("Essential Verbs")
}
