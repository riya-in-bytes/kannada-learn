package com.radriver.kannada.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.Level
import kotlinx.coroutines.flow.Flow

/**
 * Per-category progress row for the dashboard, produced by a single
 * GROUP BY query instead of one query per category.
 */
data class CategoryProgress(
    val category: Category,
    val total: Int,
    val mastered: Int
)

/** Per-level progress row for the dashboard, same shape as [CategoryProgress]. */
data class LevelProgress(
    val level: Level,
    val total: Int,
    val mastered: Int
)

/**
 * All operations are either suspend functions or return Flow, so Room
 * never touches the main thread ([androidx.room.RoomDatabase.Builder.allowMainThreadQueries]
 * is intentionally never enabled).
 */
@Dao
interface WordDao {

    @Query("SELECT * FROM words ORDER BY id")
    fun getAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE category = :category ORDER BY id")
    fun getByCategory(category: Category): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE level = :level ORDER BY id")
    fun getByLevel(level: Level): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE level = :level AND category = :category ORDER BY id")
    fun getByLevelAndCategory(level: Level, category: Category): Flow<List<WordEntity>>

    /** One-shot full snapshot used to build a quiz in memory (no N+1 queries). */
    @Query("SELECT * FROM words")
    suspend fun getAllOnce(): List<WordEntity>

    @Query("UPDATE words SET correctCount = correctCount + 1, lastAnsweredEpochDay = :todayEpochDay WHERE id = :id")
    suspend fun markCorrect(id: Int, todayEpochDay: Long)

    @Query("UPDATE words SET wrongCount = wrongCount + 1, lastAnsweredEpochDay = :todayEpochDay WHERE id = :id")
    suspend fun markWrong(id: Int, todayEpochDay: Long)

    @Query("SELECT COUNT(*) FROM words WHERE correctCount >= :threshold")
    fun masteredCount(threshold: Int): Flow<Int>

    /**
     * The words behind [masteredCount] — same predicate, so the list and the
     * counter can never disagree. Not ordered by category: that column stores
     * the enum *name*, so SQL would sort it alphabetically; callers that want
     * category order group and sort by ordinal in Kotlin.
     */
    @Query("SELECT * FROM words WHERE correctCount >= :threshold ORDER BY english COLLATE NOCASE")
    fun masteredWords(threshold: Int): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM words")
    fun totalCount(): Flow<Int>

    @Query(
        """
        SELECT category,
               COUNT(*) AS total,
               SUM(CASE WHEN correctCount >= :threshold THEN 1 ELSE 0 END) AS mastered
        FROM words
        GROUP BY category
        """
    )
    fun categoryProgress(threshold: Int): Flow<List<CategoryProgress>>

    @Query(
        """
        SELECT level,
               COUNT(*) AS total,
               SUM(CASE WHEN correctCount >= :threshold THEN 1 ELSE 0 END) AS mastered
        FROM words
        GROUP BY level
        """
    )
    fun levelProgress(threshold: Int): Flow<List<LevelProgress>>

    /**
     * Leitner-lite review queue: words whose interval (by box = MIN(correctCount, 3))
     * has elapsed since they were last answered, most-struggled and stalest first.
     * The CASE intervals mirror [com.radriver.kannada.domain.model.REVIEW_INTERVAL_DAYS]
     * and must stay identical to the one in [dueForReviewCount].
     */
    @Query(
        """
        SELECT * FROM words
        WHERE lastAnsweredEpochDay IS NOT NULL
          AND :todayEpochDay - lastAnsweredEpochDay >=
              CASE MIN(correctCount, 3) WHEN 0 THEN 1 WHEN 1 THEN 2 WHEN 2 THEN 4 ELSE 7 END
        ORDER BY (correctCount - wrongCount) ASC, lastAnsweredEpochDay ASC
        LIMIT :limit
        """
    )
    suspend fun dueForReview(todayEpochDay: Long, limit: Int): List<WordEntity>

    /** Same predicate as [dueForReview]; re-emits on writes, not at midnight — acceptable staleness. */
    @Query(
        """
        SELECT COUNT(*) FROM words
        WHERE lastAnsweredEpochDay IS NOT NULL
          AND :todayEpochDay - lastAnsweredEpochDay >=
              CASE MIN(correctCount, 3) WHEN 0 THEN 1 WHEN 1 THEN 2 WHEN 2 THEN 4 ELSE 7 END
        """
    )
    fun dueForReviewCount(todayEpochDay: Long): Flow<Int>

    /** Stable id-ordered lookup for the deterministic word-of-the-day pick. */
    @Query("SELECT * FROM words ORDER BY id LIMIT 1 OFFSET :offset")
    suspend fun wordAtOffset(offset: Int): WordEntity?

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(words: List<WordEntity>)
}
