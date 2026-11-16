package com.riyainbytes.pocketshaala.data.session

import com.riyainbytes.pocketshaala.domain.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The language currently being learned. App-scoped (owned by AppContainer)
 * so every tab's ViewModel observes one source of truth.
 *
 * Deliberately in memory only: the app always opens in [Language.DEFAULT]
 * (Kannada) and another language is opt-in per session. Because Android can
 * keep the process alive across a "relaunch" (back out, swipe from recents),
 * MainActivity also calls [switchTo] with the default on every genuine launch.
 */
class LanguageSession {

    private val _current = MutableStateFlow(Language.DEFAULT)
    val current: StateFlow<Language> = _current.asStateFlow()

    fun switchTo(language: Language) {
        _current.value = language
    }
}
