package com.radriver.kannada.data.repository

import com.radriver.kannada.data.local.dao.CategoryProgress
import com.radriver.kannada.data.local.dao.WordDao
import com.radriver.kannada.data.prefs.UserPrefs
import com.radriver.kannada.domain.model.MASTERY_THRESHOLD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Everything the dashboard needs, produced as one cold flow. */
data class DashboardData(
    val currentStreak: Int,
    val longestStreak: Int,
    val masteredWords: Int,
    val totalWords: Int,
    val categoryProgress: List<CategoryProgress>
)

class ProgressRepository(
    private val wordDao: WordDao,
    private val userPrefs: UserPrefs
) {

    /** Combined stream of streak metrics (DataStore) and mastery counts (Room). */
    val dashboardData: Flow<DashboardData> = combine(
        userPrefs.streakInfo,
        wordDao.masteredCount(MASTERY_THRESHOLD),
        wordDao.totalCount(),
        wordDao.categoryProgress(MASTERY_THRESHOLD)
    ) { streak, mastered, total, categories ->
        DashboardData(
            currentStreak = streak.currentStreak,
            longestStreak = streak.longestStreak,
            masteredWords = mastered,
            totalWords = total,
            categoryProgress = categories
        )
    }

    /** Called for every correct quiz answer; mastery falls out of the count. */
    suspend fun onCorrectAnswer(wordId: Int) = wordDao.incrementCorrect(wordId)

    /** Called on every app foreground; keeps the daily streak up to date. */
    suspend fun recordAppOpen() = userPrefs.recordAppOpen()
}
