package com.riyainbytes.pocketshaala.data.local

import com.riyainbytes.pocketshaala.data.local.entity.WordEntity
import com.riyainbytes.pocketshaala.domain.model.Category
import com.riyainbytes.pocketshaala.domain.model.romanizationKey

/**
 * The embedded vocabulary, split by language and by the database version
 * that introduced it: Kannada [v1Words] shipped with DB v1, [v2Words] and
 * [v3Words] came with the v2/v3 migrations, and [gujaratiV1Words] with v4.
 * Fresh installs seed [words] (everything) in [AppDatabase]'s onCreate
 * callback; upgrading installs get only each migration's own list.
 *
 * Kannada transliteration scheme (the Gujarati list follows the same one):
 *  - long "aa" (ಾ), "ee" (ೀ), "oo" (ೂ) vowels are doubled;
 *  - Kannada ē/ō are written as plain e/o (closest English reading);
 *  - aspirates keep h (dh, bh, th); retroflex consonants are unmarked.
 */
object SeedData {

    /**
     * FROZEN: shipped in DB v1 (46 words: 16 greetings/phrases, numbers 1-15,
     * 15 essential verbs), all BEGINNER. Never edit or reorder these entries —
     * installed databases already contain these rows. New vocabulary goes in
     * [SeedDataV2].
     */
    val v1Words: List<WordEntity> = buildList {
        // ---- Greetings & essential phrases ----
        greeting("Hello", "ನಮಸ್ಕಾರ", "Namaskaara")
        greeting("Welcome", "ಸುಸ್ವಾಗತ", "Susvaagata")
        greeting("How are you?", "ಹೇಗಿದ್ದೀರಾ?", "Hegiddeeraa?")
        greeting("I am fine", "ನಾನು ಚೆನ್ನಾಗಿದ್ದೇನೆ", "Naanu chennaagiddene")
        greeting("Thank you", "ಧನ್ಯವಾದಗಳು", "Dhanyavaadagalu")
        greeting("Please", "ದಯವಿಟ್ಟು", "Dayavittu")
        greeting("Sorry / Excuse me", "ಕ್ಷಮಿಸಿ", "Kshamisi")
        greeting("Yes", "ಹೌದು", "Haudu")
        greeting("No", "ಇಲ್ಲ", "Illa")
        greeting("Okay", "ಸರಿ", "Sari")
        greeting("Goodbye", "ಹೋಗಿ ಬನ್ನಿ", "Hogi banni")
        greeting("See you again", "ಮತ್ತೆ ಸಿಗೋಣ", "Matte sigona")
        greeting("What is your name?", "ನಿಮ್ಮ ಹೆಸರೇನು?", "Nimma hesarenu?")
        greeting("My name is…", "ನನ್ನ ಹೆಸರು…", "Nanna hesaru…")
        greeting("Good morning", "ಶುಭೋದಯ", "Shubhodaya")
        greeting("Good night", "ಶುಭ ರಾತ್ರಿ", "Shubha raatri")

        // ---- Numbers 1-15 (word forms — what you say aloud) ----
        number("One", "ಒಂದು", "Ondu")
        number("Two", "ಎರಡು", "Eradu")
        number("Three", "ಮೂರು", "Mooru")
        number("Four", "ನಾಲ್ಕು", "Naalku")
        number("Five", "ಐದು", "Aidu")
        number("Six", "ಆರು", "Aaru")
        number("Seven", "ಏಳು", "Elu")
        number("Eight", "ಎಂಟು", "Entu")
        number("Nine", "ಒಂಬತ್ತು", "Ombattu")
        number("Ten", "ಹತ್ತು", "Hattu")
        number("Eleven", "ಹನ್ನೊಂದು", "Hannondu")
        number("Twelve", "ಹನ್ನೆರಡು", "Hanneradu")
        number("Thirteen", "ಹದಿಮೂರು", "Hadimooru")
        number("Fourteen", "ಹದಿನಾಲ್ಕು", "Hadinaalku")
        number("Fifteen", "ಹದಿನೈದು", "Hadinaidu")

        // ---- Essential verbs (dictionary/root forms) ----
        verb("To eat", "ತಿನ್ನು", "Tinnu")
        verb("To drink", "ಕುಡಿ", "Kudi")
        verb("To go", "ಹೋಗು", "Hogu")
        verb("To come", "ಬರು", "Baru")
        verb("To see", "ನೋಡು", "Nodu")
        verb("To speak", "ಮಾತನಾಡು", "Maatanaadu")
        verb("To listen / ask", "ಕೇಳು", "Kelu")
        verb("To give", "ಕೊಡು", "Kodu")
        verb("To take", "ತೆಗೆದುಕೊಳ್ಳು", "Tegedukollu")
        verb("To do / make", "ಮಾಡು", "Maadu")
        verb("To sleep", "ಮಲಗು", "Malagu")
        verb("To sit", "ಕುಳಿತುಕೊಳ್ಳು", "Kulitukollu")
        verb("To walk", "ನಡೆ", "Nade")
        verb("To read", "ಓದು", "Odu")
        verb("To write", "ಬರೆ", "Bare")
    }

    /** Added in DB v2 — see [SeedDataV2]. */
    val v2Words: List<WordEntity> = SeedDataV2.words

    /** Added in DB v3 — see [SeedDataV3]. */
    val v3Words: List<WordEntity> = SeedDataV3.words

    /** Every Kannada word, in id order. */
    val kannadaWords: List<WordEntity> = v1Words + v2Words + v3Words

    /** Added in DB v4 — see [SeedDataGujarati]. */
    val gujaratiV1Words: List<WordEntity> = SeedDataGujarati.words

    /** The full vocabulary a fresh install seeds. */
    val words: List<WordEntity> = kannadaWords + gujaratiV1Words

    init {
        // Fail fast during development instead of silently shrinking quiz
        // option sets (first access happens off the main thread, from seeding).
        checkSeedInvariants(words)
        checkMirror(kannadaWords, gujaratiV1Words, SeedDataGujarati.ENGLISH_OVERRIDES)
    }

    private fun MutableList<WordEntity>.greeting(en: String, kn: String, tr: String) =
        add(WordEntity(english = en, script = kn, transliteration = tr, category = Category.GREETINGS))

    private fun MutableList<WordEntity>.number(en: String, kn: String, tr: String) =
        add(WordEntity(english = en, script = kn, transliteration = tr, category = Category.NUMBERS))

    private fun MutableList<WordEntity>.verb(en: String, kn: String, tr: String) =
        add(WordEntity(english = en, script = kn, transliteration = tr, category = Category.VERBS))
}

/**
 * buildQuiz dedupes options by script, English and romanization, so seed
 * duplicates would silently shrink option sets. Checked per language —
 * mirrored vocabularies share their English by design. Romanizations must be
 * unique only where they are the visible text (languages that don't show
 * their script); Kannada legitimately repeats one ("Anna").
 */
internal fun checkSeedInvariants(words: List<WordEntity>) {
    words.groupBy { it.language }.forEach { (language, list) ->
        list.requireUnique("$language script") { it.script }
        list.requireUnique("$language english") { it.english }
        if (!language.showsScript) {
            list.requireUnique("$language romanization") { romanizationKey(it.transliteration) }
        }
    }
}

/**
 * [mirror] must be [source] entry-for-entry: same (english, category, level),
 * in the same order, with english changed only through [englishOverrides].
 * Number words can never be overridden — quiz filtering
 * ([com.riyainbytes.pocketshaala.data.repository.WordRepository.ALLOWED_QUIZ_NUMBERS])
 * and number ordering ([com.riyainbytes.pocketshaala.domain.model.numberDisplayRank])
 * match on them. Keeps the languages' categories, levels and progress bars
 * comparable, and catches a dropped or misplaced entry at first launch.
 */
internal fun checkMirror(
    source: List<WordEntity>,
    mirror: List<WordEntity>,
    englishOverrides: Map<String, String>
) {
    val overriddenNumbers = source.filter {
        it.category == Category.NUMBERS && it.english in englishOverrides
    }
    check(overriddenNumbers.isEmpty()) {
        "Number words can't be overridden: ${overriddenNumbers.map { it.english }}"
    }
    val unknownOverrides = englishOverrides.keys - source.map { it.english }.toSet()
    check(unknownOverrides.isEmpty()) { "Overrides for words not in the source list: $unknownOverrides" }

    val expected = source.map { Triple(englishOverrides[it.english] ?: it.english, it.category, it.level) }
    val actual = mirror.map { Triple(it.english, it.category, it.level) }
    // A missing entry shows up as a mismatch at its index; extra trailing
    // entries as a mismatch just past the end of the source.
    val mismatchAt = expected.indices.firstOrNull { expected[it] != actual.getOrNull(it) }
        ?: expected.size.takeIf { actual.size > it }
    check(mismatchAt == null) {
        "Mirror doesn't match its source (${actual.size} of ${expected.size} entries); first difference at " +
            "index $mismatchAt: expected ${expected.getOrNull(mismatchAt!!)}, got ${actual.getOrNull(mismatchAt)}"
    }
}

private fun <K> List<WordEntity>.requireUnique(what: String, key: (WordEntity) -> K) {
    val duplicates = groupBy(key).filterValues { it.size > 1 }.keys
    check(duplicates.isEmpty()) { "Duplicate $what in seed data: $duplicates" }
}
