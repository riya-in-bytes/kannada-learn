package com.radriver.kannada.data.repository

import com.radriver.kannada.data.local.dao.WordDao
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.Level
import com.radriver.kannada.domain.model.MASTERY_THRESHOLD
import com.radriver.kannada.domain.model.QuizQuestion
import com.radriver.kannada.domain.model.QuizWord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class WordRepository(private val wordDao: WordDao) {

    companion object {
        const val MIN_QUIZ_SIZE = 5
        const val MAX_QUIZ_SIZE = 10
        private const val DISTRACTORS_PER_QUESTION = 3

        /** A quiz needs at least one correct answer + three distractors. */
        const val MIN_WORDS_FOR_QUIZ = DISTRACTORS_PER_QUESTION + 1

        /**
         * The only [Category.NUMBERS] words allowed into quizzes/reviews:
         * 1-10, the tens through 100, 500 and 1000. All other numbers
         * (11-19, 21-29, zero, …) stay browsable in flashcards but are
         * filtered out of quizzes so drilling doesn't fixate on them.
         * An allow-list (not a block-list) means any future off-list number
         * is excluded automatically. Matched by [WordEntity.english].
         */
        val ALLOWED_QUIZ_NUMBERS = setOf(
            "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten",
            "Twenty", "Thirty", "Forty", "Fifty",
            "Sixty", "Seventy", "Eighty", "Ninety",
            "Hundred", "Five hundred", "Thousand"
        )
    }

    /**
     * Drops the number words that shouldn't appear in quizzes (see
     * [ALLOWED_QUIZ_NUMBERS]). Applied to the whole quiz pool so excluded
     * numbers can't sneak in as distractors either. Non-number categories
     * pass through untouched.
     */
    private fun List<WordEntity>.forQuiz(): List<WordEntity> = filterNot {
        it.category == Category.NUMBERS && it.english !in ALLOWED_QUIZ_NUMBERS
    }

    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAll()

    /** Filtered word stream; null level/category means "all". */
    fun getWords(level: Level?, category: Category?): Flow<List<WordEntity>> = when {
        level != null && category != null -> wordDao.getByLevelAndCategory(level, category)
        level != null -> wordDao.getByLevel(level)
        category != null -> wordDao.getByCategory(category)
        else -> wordDao.getAll()
    }

    /** Every word the learner has mastered, alphabetical by English. */
    fun masteredWords(): Flow<List<WordEntity>> = wordDao.masteredWords(MASTERY_THRESHOLD)

    /**
     * Builds a randomized multiple-choice quiz from the local database.
     *
     * The whole word list is loaded once and questions are assembled in
     * memory (no per-question queries). Question words come from [level]
     * (null = all levels); distractors prefer the same level + category,
     * then the same level, then the global pool — so number questions get
     * number distractors and level pools stay coherent without ever being
     * too small. Option text is kept unique per question by both Kannada
     * and English (options are rendered in either language depending on
     * quiz mode), and sizes are defensively clamped so a degenerate pool
     * can never produce a broken quiz.
     */
    suspend fun buildQuiz(requestedSize: Int, level: Level? = null): List<QuizQuestion> {
        val pool = wordDao.getAllOnce().forQuiz()
        val candidates = if (level != null) pool.filter { it.level == level } else pool
        if (candidates.size < MIN_WORDS_FOR_QUIZ) return emptyList()

        val size = requestedSize
            .coerceIn(MIN_QUIZ_SIZE, MAX_QUIZ_SIZE)
            .coerceAtMost(candidates.size)

        return toQuestions(candidates.shuffled().take(size), pool)
    }

    /**
     * Builds a Leitner-lite review session from words whose interval has
     * elapsed (see [WordDao.dueForReview]), most-struggled first. Unlike
     * [buildQuiz] there is no minimum size — reviewing a single due word is
     * a valid session — but the global pool must still be able to supply
     * distractors. Returns empty when nothing is due.
     */
    suspend fun buildReviewQuiz(
        maxSize: Int = MAX_QUIZ_SIZE,
        today: LocalDate = LocalDate.now()
    ): List<QuizQuestion> {
        val pool = wordDao.getAllOnce().forQuiz()
        if (pool.size < MIN_WORDS_FOR_QUIZ) return emptyList()

        // Same number filter as buildQuiz: off-list numbers aren't reviewed.
        val due = wordDao.dueForReview(today.toEpochDay(), maxSize).forQuiz()
        // The DAO already orders by most-struggled; shuffle only the
        // presentation order so repeated sessions don't feel identical.
        return toQuestions(due.shuffled(), pool)
    }

    /**
     * Live count of words due for review today (drives badges/buttons).
     * flow{} defers LocalDate.now() to collection time so long-lived
     * collectors get a fresh "today" whenever they re-subscribe.
     */
    fun dueForReviewCount(): Flow<Int> =
        flow { emitAll(wordDao.dueForReviewCount(LocalDate.now().toEpochDay())) }

    /**
     * Deterministic date-based pick over the id-ordered word list: the same
     * word all day, a new one each day. Emits again on table writes so a
     * mastery change to today's word stays live; the date is read per
     * emission, not captured. Null only while the table is empty, which
     * cannot happen after seeding.
     */
    fun wordOfTheDay(): Flow<WordEntity?> =
        wordDao.totalCount().map { count ->
            if (count == 0) null
            else wordDao.wordAtOffset((LocalDate.now().toEpochDay() % count).toInt())
        }

    private fun toQuestions(
        candidates: List<WordEntity>,
        distractorPool: List<WordEntity>
    ): List<QuizQuestion> = candidates.map { correct ->
        fun WordEntity.collides(other: WordEntity) =
            id == other.id || kannada == other.kannada || english == other.english

        val sameLevelAndCategory = distractorPool.filter {
            !it.collides(correct) && it.level == correct.level && it.category == correct.category
        }
        val sameLevel = distractorPool.filter {
            !it.collides(correct) && it.level == correct.level && it.category != correct.category
        }
        val rest = distractorPool.filter {
            !it.collides(correct) && it.level != correct.level
        }
        val distractors =
            (sameLevelAndCategory.shuffled() + sameLevel.shuffled() + rest.shuffled())
                .distinctBy { it.kannada }
                .distinctBy { it.english }
                .take(DISTRACTORS_PER_QUESTION)

        val options = (distractors + correct)
            .map {
                QuizWord(
                    wordId = it.id,
                    english = it.english,
                    kannada = it.kannada,
                    transliteration = it.transliteration
                )
            }
            .shuffled()

        QuizQuestion(
            correctWordId = correct.id,
            prompt = QuizWord(
                wordId = correct.id,
                english = correct.english,
                kannada = correct.kannada,
                transliteration = correct.transliteration
            ),
            options = options
        )
    }
}
