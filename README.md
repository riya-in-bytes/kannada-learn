# Kannada Learn — ಕನ್ನಡ ಕಲಿ

A 100% local-first Android app for English speakers learning Kannada. No network
permission, no external database, no paid APIs — vocabulary lives in an on-device
Room (SQLite) database, progress metrics in Preferences DataStore, and
pronunciation comes from Android's native TextToSpeech engine set to the
Kannada (`kn`) locale.

## Features

- **Flashcards** — 46 starter words/phrases (Greetings, Numbers 1–15, Essential
  Verbs), each showing English, Kannada script, and an informal transliteration,
  with a speaker button for native TTS pronunciation and category filter chips.
- **Quiz** — a randomized 5–10 question multiple-choice quiz built from the local
  database. Distractors are drawn from the same category first (number questions
  get number distractors), options show script + transliteration + speaker, and
  answers get instant color feedback with a score screen at the end.
- **Progress dashboard** — consecutive daily streak (current + longest) and
  words-mastered counts, overall and per category. A word is mastered after
  **3 correct quiz answers** (`MASTERY_THRESHOLD` in `domain/model/Mastery.kt`).
- **Gujarati (second language)** — the **Languages** tab switches every screen
  to a Gujarati mirror of the Kannada vocabulary, with its own progress. Gujarati
  is shown in English letters only: its script is stored solely so the `gu`
  TTS voice can pronounce it. The app always opens in Kannada.

## Architecture

Structured MVVM, single `app` module, manual DI (no Hilt):

```
ui/          Compose screens (stateless) + ViewModels (StateFlow, collectAsStateWithLifecycle)
data/
  local/     Room: WordEntity, WordDao, AppDatabase (seeds synchronously & transactionally in onCreate)
  prefs/     DataStore: streak metrics (atomic single-edit read-modify-write)
  repository/ WordRepository (vocab + quiz builder), ProgressRepository (dashboard flow)
domain/model/ Category, QuizQuestion, MASTERY_THRESHOLD
util/        TtsManager (app-scoped, StateFlow<TtsStatus>), appViewModelFactory
```

Key decisions:

- **Seeding** happens with raw SQL inside `RoomDatabase.Callback.onCreate`, which
  runs inside SQLiteOpenHelper's creation transaction — atomic, idempotent, and
  finished before the first query returns, with no main-thread I/O (every DAO
  method is `suspend` or returns `Flow`).
- **TTS** is app-scoped (owned by `AppContainer`, built with the application
  context) so rotations never tear down the engine. It tries `kn-IN` then `kn`,
  and exposes `INITIALIZING / READY / UNAVAILABLE` — screens render a hint and
  disable speaker buttons when the device has no Kannada voice.
- **Bottom navigation** uses the canonical `popUpTo(startDestination){saveState}` +
  `launchSingleTop` + `restoreState` pattern, so each tab keeps its ViewModel
  (an in-progress quiz survives tab switches and rotation).
- **Quiz state** is built imperatively by `startQuiz()` into a `MutableStateFlow`
  — never derived via `stateIn`, which would re-randomize the quiz whenever the
  subscriber count bounced.
- **`java.time`** on minSdk 24 is handled by core-library desugaring.

## Building & running

Requirements: **Android Studio Koala 2024.1.1 or newer** (AGP 8.5.2), an
emulator or device on **API 24+**.

1. Open the project folder in Android Studio and let Gradle sync.
2. Run the `app` configuration.

Command-line note: `./gradlew` works only after the binary
`gradle/wrapper/gradle-wrapper.jar` exists — it isn't checked in as it can't be
authored as text. Android Studio doesn't need it (it syncs from
`gradle-wrapper.properties`); to generate it for CLI use, run `gradle wrapper`
with any local Gradle, or copy the jar from any Gradle 8.x project.

## Verification checklist

1. First launch → Dashboard shows streak **1**, mastered **0 of 46**.
2. Flashcards → Kannada script renders with transliteration; tapping 🔊 speaks
   the word (see TTS note below); filter chips narrow to each category.
3. Quiz → pick 5–10 questions, answer all, score appears; answer the same word
   correctly in 3 quizzes and the dashboard's mastered count increases.
4. Rotate mid-quiz → quiz position and score survive. Switch tabs mid-quiz and
   return → still there.
5. Next calendar day, open the app → streak becomes 2 (any gap resets to 1).

Gujarati:

6. Languages tab → both languages show their own mastered/total and due counts;
   **Switch** on Gujarati jumps to Home, and every title badge reads "Gujarati"
   (tapping a badge opens the Languages tab). Accent colours change.
7. No Gujarati script anywhere — word of the day, flashcards, all three quiz
   modes (including the listening reveal), mistakes and the mastered list show
   the romanization only; quiz options are four distinct romanizations.
8. With the Gujarati voice installed, speaker buttons and Listening mode work;
   without it the hint names Gujarati, Listening is disabled (a selected
   Listening mode falls back to EN → GU), and Kannada speech still works after
   switching back.
9. Switch language mid-quiz (or while "building") → the quiz resets to a Setup
   in the new language with that language's due count. Rotation keeps Gujarati.
10. Back out (Back on Home) / swipe the app away, then relaunch → it opens in
    **Kannada**. Pressing Home and returning resumes where you were (Gujarati).
11. Upgrade over an existing install (`adb install -r`, same signing key) →
    Kannada progress intact, Gujarati added at 0 mastered.

## Device notes

- **Kannada / Gujarati TTS voices**: Google's TTS engine usually needs each
  voice's data installed (Settings → Accessibility → Text-to-speech). Without it
  the app shows an inline hint and disables speaker buttons instead of failing.
- **Kannada script rendering**: AOSP ships Noto Sans Kannada since Lollipop, so
  script renders on effectively all API 24+ devices; a rare stripped-down OEM
  build without Indic fonts would show tofu boxes.
- The app requests **no permissions** at all.
