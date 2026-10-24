package com.radriver.kannada.util

import android.content.Context
import android.speech.tts.TextToSpeech
import com.radriver.kannada.domain.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger

enum class TtsStatus {
    /** Engine not created yet — created lazily on first use. */
    UNINITIALIZED,

    /** Engine binding / language check in flight. */
    INITIALIZING,

    /** Voice for [TtsManager.voiceLanguage] ready; speak() will produce audio. */
    READY,

    /**
     * Engine failed (permanent), or no voice for [TtsManager.voiceLanguage] on
     * this device (re-checked when the language changes); speak() is a no-op.
     */
    UNAVAILABLE
}

/**
 * App-scoped wrapper around Android's native [TextToSpeech], voicing the
 * language currently being learned.
 *
 * Owned by the AppContainer (never an Activity) and built with the
 * application context, so configuration changes neither leak the Activity nor
 * tear down and re-initialize the engine — the post-rotation "speaker taps
 * silently do nothing" window doesn't exist. The engine lives for the process
 * lifetime; the OS reclaims it with the process, so no explicit shutdown()
 * call is needed.
 *
 * One engine serves every language: [useLanguage] re-points it, and [status]
 * always describes the voice for [voiceLanguage]. Before the engine is ready
 * a language change is only recorded — [status] stays UNINITIALIZED until
 * [initialize] creates the engine, which then applies the recorded language.
 */
class TtsManager(context: Context) {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private val utteranceSeq = AtomicInteger(0)

    /** Guards [engineReady]/[applied] and every setLanguage call (onInit's thread is unspecified). */
    private val lock = Any()

    /** True once onInit succeeded; until then a language change is only recorded. */
    private var engineReady = false

    /** The language the engine was last pointed at (even if it had no voice). */
    private var applied: Language? = null

    /** Spoken as soon as the engine becomes ready, if a tap raced initialization. */
    @Volatile
    private var pending: Pair<String, Language>? = null

    private val _status = MutableStateFlow(TtsStatus.UNINITIALIZED)
    val status: StateFlow<TtsStatus> = _status.asStateFlow()

    private val _voiceLanguage = MutableStateFlow(Language.DEFAULT)

    /** The language [status] describes — what the UI names in "voice not available". */
    val voiceLanguage: StateFlow<Language> = _voiceLanguage.asStateFlow()

    /**
     * Creates the engine if it doesn't exist yet. Safe to call repeatedly and
     * from any thread (guarded by an atomic state transition).
     */
    fun initialize() {
        if (!_status.compareAndSet(TtsStatus.UNINITIALIZED, TtsStatus.INITIALIZING)) return

        tts = TextToSpeech(appContext) { initResult ->
            if (initResult != TextToSpeech.SUCCESS) {
                _status.value = TtsStatus.UNAVAILABLE
                return@TextToSpeech
            }
            synchronized(lock) {
                engineReady = true
                applyLocked(_voiceLanguage.value)
            }
            pending?.let { (text, language) ->
                pending = null
                speak(text, language)
            }
        }
    }

    /**
     * Points the voice at [language]. Idempotent, so it can be driven straight
     * from the active-language flow (and re-run on every Activity recreation).
     */
    fun useLanguage(language: Language) {
        synchronized(lock) {
            if (_voiceLanguage.value == language && (applied == language || !engineReady)) return
            _voiceLanguage.value = language
            pending = null
            if (engineReady) applyLocked(language)
        }
    }

    /** Caller holds [lock] and the engine is ready. Prefers the regional locale. */
    private fun applyLocked(language: Language) {
        val engine = requireNotNull(tts)
        engine.stop()
        val supported = language.ttsLocales.any { isLanguageUsable(engine.setLanguage(it)) }
        applied = language
        _status.value = if (supported) TtsStatus.READY else TtsStatus.UNAVAILABLE
    }

    /**
     * Speaks [text] (native script) with [language]'s voice. Each call
     * interrupts the previous utterance (QUEUE_FLUSH) — tapping a new word
     * shouldn't queue behind the old one. Never throws; does nothing when the
     * voice is unavailable, or when [language] isn't the voice's current
     * language (a stale tap racing a switch must not be read by the wrong voice).
     */
    fun speak(text: String, language: Language) {
        when (_status.value) {
            TtsStatus.READY -> {
                if (language != synchronized(lock) { applied }) return
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "word-${utteranceSeq.incrementAndGet()}")
            }

            TtsStatus.UNINITIALIZED -> {
                pending = text to language
                initialize()
            }

            TtsStatus.INITIALIZING -> pending = text to language

            TtsStatus.UNAVAILABLE -> Unit
        }
    }

    /** Stops any in-flight utterance (e.g. when the app goes to background). */
    fun stop() {
        tts?.stop()
    }

    private fun isLanguageUsable(setLanguageResult: Int): Boolean =
        setLanguageResult != TextToSpeech.LANG_MISSING_DATA &&
            setLanguageResult != TextToSpeech.LANG_NOT_SUPPORTED
}
