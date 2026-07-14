package com.radriver.kannada.data.repository

import com.radriver.kannada.data.local.dao.WordDao
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.QuizOption
import com.radriver.kannada.domain.model.QuizQuestion
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {

    companion object {
        const val MIN_QUIZ_SIZE = 5
        const val MAX_QUIZ_SIZE = 10
        private const val DISTRACTORS_PER_QUESTION = 3

        /** A quiz needs at least one correct answer + three distractors. */
        const val MIN_WORDS_FOR_QUIZ = DISTRACTORS_PER_QUESTION + 1
    }

    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAll()

    fun getWordsByCategory(category: Category): Flow<List<WordEntity>> =
        wordDao.getByCategory(category)

    /**
     * Builds a randomized multiple-choice quiz from the local database.
     *
     * The whole word list is loaded once and questions are assembled in
     * memory (no per-question queries). Distractors are drawn from the same
     * category first — so number questions get number distractors — topped
     * up from the global pool if the category is too small. Option text is
     * kept unique per question, and sizes are defensively clamped so a
     * degenerate pool can never produce a broken quiz.
     */
    suspend fun buildQuiz(requestedSize: Int): List<QuizQuestion> {
        val pool = wordDao.getAllOnce()
        if (pool.size < MIN_WORDS_FOR_QUIZ) return emptyList()

        val size = requestedSize
            .coerceIn(MIN_QUIZ_SIZE, MAX_QUIZ_SIZE)
            .coerceAtMost(pool.size)

        return pool.shuffled().take(size).map { correct ->
            val sameCategory = pool.filter {
                it.id != correct.id && it.category == correct.category && it.kannada != correct.kannada
            }
            val otherCategories = pool.filter {
                it.id != correct.id && it.category != correct.category && it.kannada != correct.kannada
            }
            val distractors = (sameCategory.shuffled() + otherCategories.shuffled())
                .distinctBy { it.kannada }
                .take(DISTRACTORS_PER_QUESTION)

            val options = (distractors + correct)
                .map { QuizOption(wordId = it.id, kannada = it.kannada, transliteration = it.transliteration) }
                .shuffled()

            QuizQuestion(
                correctWordId = correct.id,
                promptEnglish = correct.english,
                options = options
            )
        }
    }
}
