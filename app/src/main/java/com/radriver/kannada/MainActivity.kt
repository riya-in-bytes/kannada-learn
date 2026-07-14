package com.radriver.kannada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import com.radriver.kannada.ui.navigation.KannadaNavGraph
import com.radriver.kannada.ui.theme.KannadaLearnTheme
import com.radriver.kannada.util.appViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as KannadaApp).container
        val viewModelFactory = appViewModelFactory(container)

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
            KannadaLearnTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KannadaNavGraph(
                        viewModelFactory = viewModelFactory,
                        ttsManager = container.ttsManager
                    )
                }
            }
        }
    }
}
