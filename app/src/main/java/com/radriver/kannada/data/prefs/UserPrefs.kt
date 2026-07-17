package com.radriver.kannada.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
}
