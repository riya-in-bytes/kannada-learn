package com.radriver.kannada.data.repository

import com.radriver.kannada.data.local.dao.CategoryProgress
import com.radriver.kannada.data.local.dao.LevelProgress
import com.radriver.kannada.data.local.dao.WordDao
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.Language
import com.radriver.kannada.domain.model.Level
import com.radriver.kannada.domain.model.romanizationKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordRepositoryTest {

    /** Serves a fixed word list, filtered by language the way the SQL does. */
    private class FakeWordDao(private val words: List<WordEntity>) : WordDao {
        private fun of(language: Language) = words.filter { it.language == language }

        override fun getAll(language: Language): Flow<List<WordEntity>> = flowOf(of(language))
        override fun getByCategory(language: Language, category: Category) =
            flowOf(of(language).filter { it.category == category })
        override fun getByLevel(language: Language, level: Level) =
            flowOf(of(language).filter { it.level == level })
        override fun getByLevelAndCategory(language: Language, level: Level, category: Category) =
            flowOf(of(language).filter { it.level == level && it.category == category })
        override suspend fun getAllOnce(language: Language) = of(language)
        override suspend fun markCorrect(id: Int, todayEpochDay: Long) = Unit
        override suspend fun markWrong(id: Int, todayEpochDay: Long) = Unit
        override fun masteredCount(language: Language, threshold: Int) = flowOf(0)
        override fun masteredWords(language: Language, threshold: Int) =
            flowOf(emptyList<WordEntity>())
        override fun totalCount(language: Language) = flowOf(of(language).size)
        override fun categoryProgress(language: Language, threshold: Int) =
            flowOf(emptyList<CategoryProgress>())
        override fun levelProgress(language: Language, threshold: Int) =
            flowOf(emptyList<LevelProgress>())
        override suspend fun dueForReview(language: Language, todayEpochDay: Long, limit: Int) =
            emptyList<WordEntity>()
        override fun dueForReviewCount(language: Language, todayEpochDay: Long) = flowOf(0)
        override suspend fun wordAtOffset(language: Language, offset: Int) = of(language)[offset]
    }

    private fun gujarati(id: Int, en: String, script: String, tr: String) = WordEntity(
        id = id,
        english = en,
        script = script,
        transliteration = tr,
        category = Category.FOOD,
        language = Language.GUJARATI
    )

    // Exactly four words, two of which differ only in case/punctuation of
    // their romanization: without romanization dedupe the "Water" question
    // is forced to show both "Doodh" and "doodh." as options.
    private val gujaratiPool = listOf(
        gujarati(1, "Water", "પાણી", "Paani"),
        gujarati(2, "Milk", "દૂધ", "Doodh"),
        gujarati(3, "Milk (village)", "દુધ", "doodh."),
        gujarati(4, "Tea", "ચા", "Chaa")
    )

    @Test
    fun `quiz options never repeat a romanization`() = runBlocking {
        val questions = WordRepository(FakeWordDao(gujaratiPool))
            .buildQuiz(Language.GUJARATI, requestedSize = 4, level = null)

        assertEquals(4, questions.size)
        questions.forEach { question ->
            val keys = question.options.map { romanizationKey(it.transliteration) }
            assertEquals("options of '${question.prompt.english}'", keys.distinct(), keys)
        }
    }

    @Test
    fun `quiz words carry their language`() = runBlocking {
        val questions = WordRepository(FakeWordDao(gujaratiPool))
            .buildQuiz(Language.GUJARATI, requestedSize = 4, level = null)

        assertTrue(questions.all { q ->
            q.prompt.language == Language.GUJARATI &&
                q.options.all { it.language == Language.GUJARATI }
        })
    }

    @Test
    fun `word of the day is unchanged for Kannada`() {
        assertEquals(
            (20_000L % 256).toInt(),
            WordRepository.wordOfTheDayOffset(20_000L, 256, Language.KANNADA)
        )
    }

    @Test
    fun `mirrored Gujarati list shows a different word of the day than Kannada`() {
        (20_000L until 20_400L).forEach { day ->
            assertNotEquals(
                WordRepository.wordOfTheDayOffset(day, 256, Language.KANNADA),
                WordRepository.wordOfTheDayOffset(day, 256, Language.GUJARATI)
            )
        }
    }
}
