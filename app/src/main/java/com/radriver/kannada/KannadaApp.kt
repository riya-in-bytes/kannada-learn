package com.radriver.kannada

import android.app.Application
import android.content.Context
import com.radriver.kannada.data.local.AppDatabase
import com.radriver.kannada.data.prefs.UserPrefs
import com.radriver.kannada.data.repository.ProgressRepository
import com.radriver.kannada.data.repository.WordRepository
import com.radriver.kannada.util.TtsManager

/**
 * Manual dependency container. Everything here is an app-scoped singleton;
 * construction is cheap (Room and DataStore both open lazily on first use).
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = AppDatabase.getInstance(appContext)
    private val userPrefs = UserPrefs(appContext)

    val wordRepository = WordRepository(database.wordDao())
    val progressRepository = ProgressRepository(database.wordDao(), userPrefs)
    val ttsManager = TtsManager(appContext)
}

class KannadaApp : Application() {
    // lazy: the Application context isn't attached during field initialization,
    // and the first access always happens after onCreate (from MainActivity).
    val container: AppContainer by lazy { AppContainer(this) }
}
