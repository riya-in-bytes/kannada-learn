package com.radriver.kannada.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.domain.model.Category
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

    /** One-shot full snapshot used to build a quiz in memory (no N+1 queries). */
    @Query("SELECT * FROM words")
    suspend fun getAllOnce(): List<WordEntity>

    @Query("UPDATE words SET correctCount = correctCount + 1 WHERE id = :id")
    suspend fun incrementCorrect(id: Int)

    @Query("SELECT COUNT(*) FROM words WHERE correctCount >= :threshold")
    fun masteredCount(threshold: Int): Flow<Int>

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

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(words: List<WordEntity>)
}
