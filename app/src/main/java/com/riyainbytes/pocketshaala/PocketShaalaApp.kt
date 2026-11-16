package com.riyainbytes.pocketshaala

import android.app.Application
import android.content.Context
import com.riyainbytes.pocketshaala.data.local.AppDatabase
import com.riyainbytes.pocketshaala.data.prefs.UserPrefs
import com.riyainbytes.pocketshaala.data.repository.ProgressRepository
import com.riyainbytes.pocketshaala.data.repository.WordRepository
import com.riyainbytes.pocketshaala.data.session.LanguageSession
import com.riyainbytes.pocketshaala.util.TtsManager

/**
 * Manual dependency container. Everything here is an app-scoped singleton;
 * construction is cheap (Room and DataStore both open lazily on first use).
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = AppDatabase.getInstance(appContext)

    val userPrefs = UserPrefs(appContext)
    val wordRepository = WordRepository(database.wordDao())
    val progressRepository = ProgressRepository(database.wordDao(), userPrefs)
    val ttsManager = TtsManager(appContext)
    val languageSession = LanguageSession()
}

class PocketShaalaApp : Application() {
    // lazy: the Application context isn't attached during field initialization,
    // and the first access always happens after onCreate (from MainActivity).
    val container: AppContainer by lazy { AppContainer(this) }
}
