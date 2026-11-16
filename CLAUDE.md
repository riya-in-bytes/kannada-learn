# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Pocket Shaala — a 100% local-first Android app (Kotlin, Jetpack Compose, single `app` module) for English speakers learning Kannada and — as a switchable second language — Gujarati (romanized only). No network permission, no external services: vocabulary lives in Room, streak/progress in Preferences DataStore, pronunciation via Android's native TextToSpeech (`kn` / `gu` locales).

## Build & run

- Intended workflow: open in **Android Studio** (Koala 2024.1.1+ / AGP 8.5.2) and run the `app` configuration on API 24+.
- **CLI caveat**: `./gradlew` fails until `gradle/wrapper/gradle-wrapper.jar` exists — it is not checked in. Generate it with `gradle wrapper` (any local Gradle 8.x) or copy from another Gradle 8.x project. Android Studio does not need it.
- Once the jar exists: `./gradlew assembleDebug`, `./gradlew assembleRelease` (release is minified + resource-shrunk).
- JVM unit tests live in `app/src/test` (seed-data invariants, quiz building, word-of-the-day offsets): `./gradlew testDebugUnitTest`. There are no instrumented tests; everything Android-bound (Room migrations, TTS, Compose UI) is verified manually — see the checklist in [README.md](README.md).
- Kotlin 2.0.20 with the compose compiler plugin; Room via **KSP** (not kapt); dependency versions centralized in [gradle/libs.versions.toml](gradle/libs.versions.toml).

## Architecture

MVVM with manual DI — **no Hilt**. The object graph is `AppContainer` in [PocketShaalaApp.kt](app/src/main/java/com/riyainbytes/pocketshaala/PocketShaalaApp.kt): app-scoped singletons (database, prefs, repositories, `TtsManager`) built lazily off the Application. Every ViewModel is registered in one `appViewModelFactory` ([util/ViewModelFactory.kt](app/src/main/java/com/riyainbytes/pocketshaala/util/ViewModelFactory.kt)) — add new ViewModels there, wired from the container.

Layers under `com.riyainbytes.pocketshaala`:

- `ui/` — Compose screens (stateless) + ViewModels exposing `StateFlow`, collected with `collectAsStateWithLifecycle`. One package per feature (dashboard, flashcards, quiz) plus `components/`, `navigation/`, `theme/`.
- `data/local/` — Room: `WordEntity`, `WordDao`, `AppDatabase` (version 4: `MIGRATION_1_2`, `MIGRATION_2_3`, `MIGRATION_3_4`), Kannada `SeedData` (46 frozen v1 words) + `SeedDataV2` (200, v2) + `SeedDataV3` (10, v3), and `SeedDataGujarati` (the Gujarati mirror of all 256, v4). Seed lists are **append-only**; vocabulary added later needs a new DB version + migration that inserts only the new list.
- `data/session/` — `LanguageSession`: the in-memory active `Language` every tab's ViewModel observes.
- `data/prefs/` — `UserPrefs` DataStore for streak metrics (shared across languages), the shared level/category filters, and the per-language word-of-the-day dismissal.
- `data/repository/` — `WordRepository` (vocab + quiz/review builders, word of the day), `ProgressRepository` (dashboard flow, per-answer mastery/review updates).
- `domain/model/` — `Language` (KANNADA default, GUJARATI; `showsScript`, TTS locales), `Category`, `Level` (BEGINNER/INTERMEDIATE/ADVANCED, free selection), `QuizQuestion`/`QuizWord`/`QuizMode` (EN→target, target→EN, listening), `Mastery` (`MASTERY_THRESHOLD = 3` correct quiz answers to master a word; `REVIEW_INTERVAL_DAYS` Leitner intervals mirrored in the DAO's SQL `CASE`).

## Load-bearing design decisions

These are deliberate; don't "fix" them without understanding why:

- **DB seeding** uses raw SQL (`ContentValues` inserts) inside `RoomDatabase.Callback.onCreate` — it runs inside SQLiteOpenHelper's creation transaction, making the seed atomic with table creation and guaranteed complete before the first query returns. Don't convert it to a coroutine + DAO call. Every DAO method must stay `suspend` or return `Flow` (this is also what keeps seeding off the main thread).
- **Quiz state** in `QuizViewModel` is built imperatively by `startQuiz()` into a `MutableStateFlow` — deliberately *not* derived with `stateIn`, which would re-randomize the quiz whenever subscriber count bounces (rotation, tab switch).
- **`TtsManager` is app-scoped** (owned by `AppContainer`, application context) so rotation never tears down the engine. One engine serves every language: `MainActivity` feeds the active language into `useLanguage`, which tries the language's `ttsLocales` (e.g. `kn-IN` then `kn`) once the engine is ready; `status` (`INITIALIZING / READY / UNAVAILABLE`) always describes `voiceLanguage`. `useLanguage` must never write `status` before the engine exists (that would stop `initialize()` from ever creating it). `speak(text, language)` is a no-op for a language other than the applied one. UI must handle `UNAVAILABLE` (hint + disabled speaker buttons), never assume a voice exists.
- **Bottom navigation** uses `popUpTo(startDestination){saveState}` + `launchSingleTop` + `restoreState` so each tab keeps its ViewModel — an in-progress quiz survives tab switches. Preserve this pattern when touching `PocketShaalaNavGraph`.
- **Streak updates** in `UserPrefs` are atomic single-`edit` read-modify-write operations.
- **`java.time` on minSdk 24** works only via core-library desugaring (enabled in [app/build.gradle.kts](app/build.gradle.kts)) — keep it if touching compile options.
- **Quiz building** (`WordRepository.buildQuiz`) loads the pool once and assembles questions in memory; question words honor the level filter, distractors prefer same level+category, then same level, then the global pool, with sizes defensively clamped. Options are unique by script, **normalized romanization** (`romanizationKey` — the only visible text for Gujarati) and English (reverse/listening modes render English options). Every DAO read is scoped to one language, so pools never mix. Keep it resilient to degenerate pools (returns empty list below 4 candidates).
- **Migrations, not destructive fallback**: `AppDatabase` registers explicit `Migration`s and must never use `fallbackToDestructiveMigration` (it would wipe per-word progress). Entity `@ColumnInfo(defaultValue)` strings must stay byte-identical to the migration's SQL `DEFAULT` clauses, or Room crashes upgrades. Both seeding paths (onCreate + migrations) share the single `insertWord` helper — new columns go there too, but **written only when the value differs from the column DEFAULT**: older migrations call the same helper against older schemas (MIGRATION_2_3 runs before `language` exists). The Kotlin property `WordEntity.script` maps to the legacy column name `kannada` (minSdk 24 has no `RENAME COLUMN`).
- **`Category`, `Level` and `Language` enums are append-only** — they're stored by name via TypeConverters; renaming/removing an entry breaks `valueOf` on existing rows.
- **Gujarati never shows its script.** `WordEntity.script` holds real Gujarati script only so the gu-IN voice pronounces it; the UI renders words exclusively through `TargetPrimaryText`/`TargetSecondaryText`/`displayText` ([ui/components/TargetWordText.kt](app/src/main/java/com/riyainbytes/pocketshaala/ui/components/TargetWordText.kt)), which decide from the **word's own** `language` (not the screen's) so a mid-switch frame can't leak a script. Outside that file, `.script` may only be passed to TTS.
- **The active language is never persisted.** `LanguageSession` starts at `Language.DEFAULT` (Kannada) and `MainActivity` resets it on every genuine launch (`savedInstanceState == null`) because Android can keep the process alive across a relaunch. Every tab ViewModel derives from `languageSession.current` (`flatMapLatest`); `QuizViewModel` resets to a fresh `Setup` on a switch and guards in-flight builds (`publishBuild`) so a stale-language quiz can't land.
- **The Gujarati list mirrors the Kannada lists** entry-for-entry (same english/category/level, in order; english may differ only via `SeedDataGujarati.ENGLISH_OVERRIDES`, never for numbers — `ALLOWED_QUIZ_NUMBERS` and `numberDisplayRank` match on English). `SeedData`'s init enforces this plus per-language uniqueness; adding Kannada vocabulary therefore means adding its Gujarati mirror in the same DB version.
- **Two names on purpose**: application label `app_name` = Pocket Shaala (Settings, installer); `MainActivity` label `app_launcher_name` = Pathshala (home screen, recents). Don't unify them.

## Constraints

- The app must stay fully offline: no network permission, no analytics, no external APIs.
- minSdk 24, targetSdk/compileSdk 34, JVM target 1.8.
- **Never run `git commit`** — the owner reviews every line and makes all commits herself. Leave changes in the working tree (staged only if asked).
