package com.riyainbytes.pocketshaala.data.local

import com.riyainbytes.pocketshaala.data.local.entity.WordEntity
import com.riyainbytes.pocketshaala.domain.model.Category
import com.riyainbytes.pocketshaala.domain.model.Language
import com.riyainbytes.pocketshaala.domain.model.Level
import org.junit.Test

class SeedDataTest {

    private fun word(
        language: Language,
        en: String,
        script: String,
        tr: String,
        category: Category = Category.GREETINGS,
        level: Level = Level.BEGINNER
    ) = WordEntity(
        english = en,
        script = script,
        transliteration = tr,
        category = category,
        level = level,
        language = language
    )

    @Test
    fun `shipped seed data passes every invariant`() {
        checkSeedInvariants(SeedData.words)
    }

    @Test
    fun `the same english word may appear once per language`() {
        checkSeedInvariants(
            listOf(
                word(Language.KANNADA, "Hello", "ನಮಸ್ಕಾರ", "Namaskaara"),
                word(Language.GUJARATI, "Hello", "નમસ્તે", "Namaste")
            )
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `duplicate english within one language is rejected`() {
        checkSeedInvariants(
            listOf(
                word(Language.GUJARATI, "Hello", "નમસ્તે", "Namaste"),
                word(Language.GUJARATI, "Hello", "કેમ છો", "Kem cho")
            )
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `romanized-only language rejects romanizations differing only in case or punctuation`() {
        checkSeedInvariants(
            listOf(
                word(Language.GUJARATI, "Yesterday", "ગઈ કાલ", "Kaal"),
                word(Language.GUJARATI, "Tomorrow", "આવતી કાલ", "kaal!")
            )
        )
    }

    private val kannada = listOf(
        word(Language.KANNADA, "Hello", "ನಮಸ್ಕಾರ", "Namaskaara"),
        word(Language.KANNADA, "One", "ಒಂದು", "Ondu", Category.NUMBERS),
        word(Language.KANNADA, "I am learning Kannada", "ನಾನು ಕನ್ನಡ ಕಲಿಯುತ್ತಿದ್ದೇನೆ", "Naanu Kannada kaliyuttiddene", Category.CONVERSATIONS, Level.ADVANCED)
    )
    private val overrides = mapOf("I am learning Kannada" to "I am learning Gujarati")

    @Test
    fun `a faithful mirror with an override passes`() {
        checkMirror(
            kannada,
            listOf(
                word(Language.GUJARATI, "Hello", "નમસ્તે", "Namaste"),
                word(Language.GUJARATI, "One", "એક", "Ek", Category.NUMBERS),
                word(Language.GUJARATI, "I am learning Gujarati", "હું ગુજરાતી શીખું છું", "Hun Gujarati sheekhun chhun", Category.CONVERSATIONS, Level.ADVANCED)
            ),
            overrides
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `a mirror missing an entry is rejected`() {
        checkMirror(
            kannada,
            listOf(
                word(Language.GUJARATI, "Hello", "નમસ્તે", "Namaste"),
                word(Language.GUJARATI, "I am learning Gujarati", "હું ગુજરાતી શીખું છું", "Hun Gujarati sheekhun chhun", Category.CONVERSATIONS, Level.ADVANCED)
            ),
            overrides
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `a mirror entry at a different level is rejected`() {
        checkMirror(
            kannada,
            listOf(
                word(Language.GUJARATI, "Hello", "નમસ્તે", "Namaste"),
                word(Language.GUJARATI, "One", "એક", "Ek", Category.NUMBERS, Level.INTERMEDIATE),
                word(Language.GUJARATI, "I am learning Gujarati", "હું ગુજરાતી શીખું છું", "Hun Gujarati sheekhun chhun", Category.CONVERSATIONS, Level.ADVANCED)
            ),
            overrides
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `number words can never be overridden`() {
        checkMirror(
            kannada,
            listOf(
                word(Language.GUJARATI, "Hello", "નમસ્તે", "Namaste"),
                word(Language.GUJARATI, "Uno", "એક", "Ek", Category.NUMBERS),
                word(Language.GUJARATI, "I am learning Gujarati", "હું ગુજરાતી શીખું છું", "Hun Gujarati sheekhun chhun", Category.CONVERSATIONS, Level.ADVANCED)
            ),
            overrides + ("One" to "Uno")
        )
    }

    @Test
    fun `script language may repeat a romanization`() {
        // Kannada legitimately has "Anna" twice (elder brother / cooked rice);
        // the visible script tells them apart.
        checkSeedInvariants(
            listOf(
                word(Language.KANNADA, "Elder brother", "ಅಣ್ಣ", "Anna"),
                word(Language.KANNADA, "Rice (cooked)", "ಅನ್ನ", "Anna")
            )
        )
    }
}
