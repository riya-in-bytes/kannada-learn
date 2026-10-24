package com.radriver.kannada.data.local

import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.Level

/**
 * Vocabulary added in DB v3: more everyday Conversations sentences, plus the
 * round number "500" (which fills the gap in the quiz-friendly number set —
 * 1000 already shipped as "Thousand" in v2) and the remaining hundreds
 * (200-900) for the Numbers tab's serial order. Inserted by MIGRATION_2_3 for
 * upgrading installs and, via [SeedData.words], by the onCreate seed for fresh
 * installs. Same transliteration scheme as [SeedData].
 *
 * Once shipped, treat this list like [SeedData.v1Words] / [SeedDataV2]:
 * append-only. Vocabulary added in a future DB v4 belongs in a new list.
 *
 * Authoring notes (for extending this list, e.g. in a follow-up session):
 *  - Use the [conversation] helper for sentences and pick INTERMEDIATE or
 *    ADVANCED explicitly — sentences are never BEGINNER (mirrors [SeedDataV2]).
 *  - Every `english` and every `kannada` string must be unique across ALL seed
 *    data (v1 + v2 + v3); the checks in [SeedData]'s init block fail fast on
 *    any duplicate.
 *  - Do NOT touch [SeedData.v1Words] or [SeedDataV2]; only append here.
 */
internal object SeedDataV3 {

    val words: List<WordEntity> = buildList {
        // ---- Round number that completes the quiz-friendly set ----
        number("Five hundred", "ಐನೂರು", "Ainooru")

        // ---- The other hundreds, so the Numbers tab counts 100, 200 … 900 ----
        number("Two hundred", "ಇನ್ನೂರು", "Innooru")
        number("Three hundred", "ಮುನ್ನೂರು", "Munnooru")
        number("Four hundred", "ನಾನೂರು", "Naanooru")
        number("Six hundred", "ಆರುನೂರು", "Aarunooru")
        number("Seven hundred", "ಏಳುನೂರು", "Elunooru")
        number("Eight hundred", "ಎಂಟುನೂರು", "Entunooru")
        number("Nine hundred", "ಒಂಬೈನೂರು", "Ombainooru")

        // ---- Conversations: more everyday sentences ----
        // Seed examples; extend this block with additional sentences.
        conversation("What is the time?", "ಸಮಯ ಎಷ್ಟು?", "Samaya eshtu?", Level.INTERMEDIATE)
        conversation("Let's go", "ಹೋಗೋಣ", "Hogona", Level.INTERMEDIATE)
    }

    private fun MutableList<WordEntity>.word(
        category: Category, level: Level, en: String, kn: String, tr: String
    ) = add(
        WordEntity(english = en, kannada = kn, transliteration = tr, category = category, level = level)
    )

    /** Numbers beyond the beginner set start at INTERMEDIATE (mirrors [SeedDataV2]). */
    private fun MutableList<WordEntity>.number(en: String, kn: String, tr: String) =
        word(Category.NUMBERS, Level.INTERMEDIATE, en, kn, tr)

    /** Sentences are never BEGINNER; the author must choose INTERMEDIATE or ADVANCED. */
    private fun MutableList<WordEntity>.conversation(en: String, kn: String, tr: String, level: Level) =
        word(Category.CONVERSATIONS, level, en, kn, tr)
}
