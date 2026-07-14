package com.radriver.kannada.util

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

enum class TtsStatus {
    /** Engine not created yet — created lazily on first use. */
    UNINITIALIZED,

    /** Engine binding / language check in flight. */
    INITIALIZING,

    /** Kannada voice ready; speak() will produce audio. */
    READY,

    /** Engine failed or no Kannada voice on this device; speak() is a no-op. */
    UNAVAILABLE
}

/**
 * App-scoped wrapper around Android's native [TextToSpeech], configured for
 * Kannada.
 *
 * Owned by the AppContainer (never an Activity) and built with the
 * application context, so configuration changes neither leak the Activity nor
 * tear down and re-initialize the engine — the post-rotation "speaker taps
 * silently do nothing" window doesn't exist. The engine lives for the process
 * lifetime; the OS reclaims it with the process, so no explicit shutdown()
 * call is needed.
 */
class TtsManager(context: Context) {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private val utteranceSeq = AtomicInteger(0)

    /** Spoken as soon as the engine becomes ready, if a tap raced initialization. */
    @Volatile
    private var pendingText: String? = null

    private val _status = MutableStateFlow(TtsStatus.UNINITIALIZED)
    val status: StateFlow<TtsStatus> = _status.asStateFlow()

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
            // Prefer the regional locale, fall back to bare language.
            val engine = requireNotNull(tts)
            val supported = isLanguageUsable(engine.setLanguage(Locale("kn", "IN"))) ||
                isLanguageUsable(engine.setLanguage(Locale("kn")))

            if (supported) {
                _status.value = TtsStatus.READY
                pendingText?.let { queued ->
                    pendingText = null
                    speak(queued)
                }
            } else {
                _status.value = TtsStatus.UNAVAILABLE
            }
        }
    }

    /**
     * Speaks [text] with the Kannada voice. Each call interrupts the previous
     * utterance (QUEUE_FLUSH) — tapping a new word shouldn't queue behind the
     * old one. Never throws; does nothing when the voice is unavailable.
     */
    fun speak(text: String) {
        when (_status.value) {
            TtsStatus.READY ->
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kn-word-${utteranceSeq.incrementAndGet()}")

            TtsStatus.UNINITIALIZED -> {
                pendingText = text
                initialize()
            }

            TtsStatus.INITIALIZING -> pendingText = text

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
