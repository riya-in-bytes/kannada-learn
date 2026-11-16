package com.riyainbytes.pocketshaala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.riyainbytes.pocketshaala.domain.model.Language
import com.riyainbytes.pocketshaala.ui.navigation.PocketShaalaNavGraph
import com.riyainbytes.pocketshaala.ui.theme.PocketShaalaTheme
import com.riyainbytes.pocketshaala.util.appViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as PocketShaalaApp).container
        val viewModelFactory = appViewModelFactory(container)

        // Genuine launches (not config-change recreations like rotation):
        // count them so the flashcard list reshuffles every few opens, and
        // start in Kannada even if the process survived a Gujarati session.
        if (savedInstanceState == null) {
            lifecycleScope.launch { container.progressRepository.incrementAppOpenCount() }
            container.languageSession.switchTo(Language.DEFAULT)
        }

        // Keep the voice on the language being learned. useLanguage is
        // idempotent, so re-collecting after a rotation is harmless.
        lifecycleScope.launch {
            container.languageSession.current.collect(container.ttsManager::useLanguage)
        }

        // Backing out of the app must make the next open a genuine launch (so
        // it starts in Kannada again): on Android 12+, Back on the root screen
        // only moves the task to the background, and the launcher icon would
        // then resume the Gujarati session. Registered before setContent, so
        // the NavHost's own Back handling (tab → start screen) still wins;
        // enabled only off the default language, leaving Kannada's Back as is.
        val leaveAppCallback = onBackPressedDispatcher.addCallback(this, enabled = false) { finish() }
        lifecycleScope.launch {
            container.languageSession.current.collect { leaveAppCallback.isEnabled = it != Language.DEFAULT }
        }

        // Warm the TTS engine early so the first speaker tap is instant.
        // The manager is app-scoped: rotations don't tear it down.
        container.ttsManager.initialize()

        // ON_RESUME: streak accounting for every foreground session (covers
        // launches, task switches, and midnight rollovers while backgrounded).
        // ON_STOP: cut any in-flight speech when the app leaves the screen.
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> lifecycleScope.launch {
                    container.progressRepository.recordAppOpen()
                }

                Lifecycle.Event.ON_STOP -> container.ttsManager.stop()

                else -> Unit
            }
        })

        setContent {
            val language by container.languageSession.current.collectAsStateWithLifecycle()
            PocketShaalaTheme(language = language) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PocketShaalaNavGraph(
                        viewModelFactory = viewModelFactory,
                        ttsManager = container.ttsManager
                    )
                }
            }
        }
    }
}
