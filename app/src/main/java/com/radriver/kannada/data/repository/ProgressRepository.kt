package com.radriver.kannada.data.repository

import com.radriver.kannada.data.local.dao.CategoryProgress
import com.radriver.kannada.data.local.dao.LevelProgress
import com.radriver.kannada.data.local.dao.WordDao
import com.radriver.kannada.data.prefs.UserPrefs
import com.radriver.kannada.domain.model.MASTERY_THRESHOLD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

/** Everything the dashboard needs, produced as one cold flow. */
data class DashboardData(
    val currentStreak: Int,
    val longestStreak: Int,
    val masteredWords: Int,
    val totalWords: Int,
    val categoryProgress: List<CategoryProgress>,
    val levelProgress: List<LevelProgress>,
    val dueForReview: Int
)

class ProgressRepository(
    private val wordDao: WordDao,
    private val userPrefs: UserPrefs
) {

    /** Intermediate holder: the Room-side counts, combined before joining DataStore. */
    private data class WordStats(
        val mastered: Int,
        val total: Int,
        val categoryProgress: List<CategoryProgress>,
        val levelProgress: List<LevelProgress>
    )

    /** Combined stream of streak metrics (DataStore) and mastery counts (Room). */
    val dashboardData: Flow<DashboardData> = combine(
        combine(
            wordDao.masteredCount(MASTERY_THRESHOLD),
            wordDao.totalCount(),
            wordDao.categoryProgress(MASTERY_THRESHOLD),
            wordDao.levelProgress(MASTERY_THRESHOLD)
        ) { mastered, total, categories, levels ->
            // GROUP BY returns rows alphabetically; present in enum order
            // (Beginner→Intermediate→Advanced, curriculum category order).
            WordStats(
                mastered,
                total,
                categories.sortedBy { it.category.ordinal },
                levels.sortedBy { it.level.ordinal }
            )
        },
        userPrefs.streakInfo,
        // flow{} defers LocalDate.now() to collection time: the repository is
        // app-scoped, so binding "today" here would freeze it at first launch.
        flow { emitAll(wordDao.dueForReviewCount(LocalDate.now().toEpochDay())) }
    ) { stats, streak, dueForReview ->
        DashboardData(
            currentStreak = streak.currentStreak,
            longestStreak = streak.longestStreak,
            masteredWords = stats.mastered,
            totalWords = stats.total,
            categoryProgress = stats.categoryProgress,
            levelProgress = stats.levelProgress,
            dueForReview = dueForReview
        )
    }

    /**
     * Called for every quiz answer. Correct answers advance mastery; wrong
     * answers bump wrongCount so the review queue surfaces the word sooner.
     * Both stamp lastAnsweredEpochDay, which starts the review clock.
     * [wordId] is always the word being tested (the correct answer), never
     * the distractor the user happened to tap.
     */
    suspend fun onAnswer(wordId: Int, correct: Boolean) {
        val today = LocalDate.now().toEpochDay()
        if (correct) wordDao.markCorrect(wordId, today) else wordDao.markWrong(wordId, today)
    }

    /** Called on every app foreground; keeps the daily streak up to date. */
    suspend fun recordAppOpen() = userPrefs.recordAppOpen()

    /** Called once per genuine launch; drives the flashcard reshuffle cadence. */
    suspend fun incrementAppOpenCount() = userPrefs.incrementAppOpenCount()
}
