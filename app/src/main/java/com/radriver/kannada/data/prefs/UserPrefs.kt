package com.radriver.kannada.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.Level
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/** Streak metrics snapshot exposed to the UI. */
data class StreakInfo(
    val currentStreak: Int,
    val longestStreak: Int
)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * User metrics persisted in Preferences DataStore.
 *
 * java.time is safe down to minSdk 24 because core-library desugaring is
 * enabled in app/build.gradle.kts.
 */
class UserPrefs(context: Context) {

    private val dataStore = context.applicationContext.dataStore

    private object Keys {
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LONGEST_STREAK = intPreferencesKey("longest_streak")
        val LAST_ACTIVE_EPOCH_DAY = longPreferencesKey("last_active_epoch_day")
        val LAST_LEVEL = stringPreferencesKey("last_level")
        val LAST_CATEGORY = stringPreferencesKey("last_category")
        val APP_OPEN_COUNT = intPreferencesKey("app_open_count")
        val WOTD_DISMISSED_EPOCH_DAY = longPreferencesKey("wotd_dismissed_epoch_day")
    }

    val streakInfo: Flow<StreakInfo> = dataStore.data
        .map { prefs ->
            StreakInfo(
                currentStreak = prefs[Keys.CURRENT_STREAK] ?: 0,
                longestStreak = prefs[Keys.LONGEST_STREAK] ?: 0
            )
        }
        .distinctUntilChanged()

    /**
     * The last level the user filtered to; null = "All". Shared by the
     * flashcards filter and the quiz setup so the choice follows the learner
     * across tabs and survives process death. A stored name no Level matches
     * (can't happen forward; defensive) also maps to null.
     */
    val selectedLevel: Flow<Level?> = dataStore.data
        .map { prefs -> Level.entries.find { it.name == prefs[Keys.LAST_LEVEL] } }
        .distinctUntilChanged()

    suspend fun setSelectedLevel(level: Level?) {
        dataStore.edit { prefs ->
            if (level == null) prefs.remove(Keys.LAST_LEVEL)
            else prefs[Keys.LAST_LEVEL] = level.name
        }
    }

    /**
     * The last category the user filtered to; null = "All". Lives here rather
     * than in the flashcards ViewModel because the dashboard writes it too —
     * tapping a "Progress by Category" row sets the filter and then switches
     * to the flashcards tab. Same defensive name lookup as [selectedLevel].
     */
    val selectedCategory: Flow<Category?> = dataStore.data
        .map { prefs -> Category.entries.find { it.name == prefs[Keys.LAST_CATEGORY] } }
        .distinctUntilChanged()

    suspend fun setSelectedCategory(category: Category?) {
        dataStore.edit { prefs ->
            if (category == null) prefs.remove(Keys.LAST_CATEGORY)
            else prefs[Keys.LAST_CATEGORY] = category.name
        }
    }

    /**
     * Seed for the flashcard shuffle: the launch count integer-divided by
     * [SHUFFLE_EVERY_N_OPENS], so it advances (and the list reshuffles) once
     * every N launches while staying constant within a run. Consumers mix in
     * the active filter so different category/level views shuffle differently.
     */
    val shuffleSeed: Flow<Long> = dataStore.data
        .map { prefs -> ((prefs[Keys.APP_OPEN_COUNT] ?: 0) / SHUFFLE_EVERY_N_OPENS).toLong() }
        .distinctUntilChanged()

    /** Counts a genuine app launch (see [SHUFFLE_EVERY_N_OPENS]). */
    suspend fun incrementAppOpenCount() {
        dataStore.edit { prefs ->
            prefs[Keys.APP_OPEN_COUNT] = (prefs[Keys.APP_OPEN_COUNT] ?: 0) + 1
        }
    }

    /** Epoch-day the Word of the Day was last dismissed; null = never. */
    val wotdDismissedEpochDay: Flow<Long?> = dataStore.data
        .map { prefs -> prefs[Keys.WOTD_DISMISSED_EPOCH_DAY] }
        .distinctUntilChanged()

    /** Hides the Word of the Day for the rest of [today]. */
    suspend fun dismissWordOfTheDay(today: LocalDate = LocalDate.now()) {
        dataStore.edit { prefs -> prefs[Keys.WOTD_DISMISSED_EPOCH_DAY] = today.toEpochDay() }
    }

    /**
     * Records that the app was used today and updates the streak:
     * same day → no change; consecutive day → streak + 1; gap (or first
     * ever launch) → streak resets to 1. Longest streak tracks the maximum.
     *
     * The whole read-modify-write happens inside a single [edit] block, so
     * concurrent callers (e.g. two rapid ON_RESUME events) cannot interleave.
     */
    suspend fun recordAppOpen(today: LocalDate = LocalDate.now()) {
        val todayEpochDay = today.toEpochDay()
        dataStore.edit { prefs ->
            val lastActive = prefs[Keys.LAST_ACTIVE_EPOCH_DAY]
            if (lastActive == todayEpochDay) return@edit

            val current = prefs[Keys.CURRENT_STREAK] ?: 0
            val newStreak = if (lastActive == todayEpochDay - 1) current + 1 else 1

            prefs[Keys.CURRENT_STREAK] = newStreak
            prefs[Keys.LONGEST_STREAK] = maxOf(newStreak, prefs[Keys.LONGEST_STREAK] ?: 0)
            prefs[Keys.LAST_ACTIVE_EPOCH_DAY] = todayEpochDay
        }
    }

    companion object {
        /** Reshuffle the flashcard list once every this many app launches. */
        const val SHUFFLE_EVERY_N_OPENS = 3
    }
}
