# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kannada Learn — a 100% local-first Android app (Kotlin, Jetpack Compose, single `app` module) for English speakers learning Kannada. No network permission, no external services: vocabulary lives in Room, streak/progress in Preferences DataStore, pronunciation via Android's native TextToSpeech (`kn` locale).

## Build & run

- Intended workflow: open in **Android Studio** (Koala 2024.1.1+ / AGP 8.5.2) and run the `app` configuration on API 24+.
- **CLI caveat**: `./gradlew` fails until `gradle/wrapper/gradle-wrapper.jar` exists — it is not checked in. Generate it with `gradle wrapper` (any local Gradle 8.x) or copy from another Gradle 8.x project. Android Studio does not need it.
- Once the jar exists: `./gradlew assembleDebug`, `./gradlew assembleRelease` (release is minified + resource-shrunk).
- There are **no tests** (`app/src/test` and `app/src/androidTest` don't exist). Verification is manual — see the checklist in [README.md](README.md).
- Kotlin 2.0.20 with the compose compiler plugin; Room via **KSP** (not kapt); dependency versions centralized in [gradle/libs.versions.toml](gradle/libs.versions.toml).

## Architecture

MVVM with manual DI — **no Hilt**. The object graph is `AppContainer` in [KannadaApp.kt](app/src/main/java/com/radriver/kannada/KannadaApp.kt): app-scoped singletons (database, prefs, repositories, `TtsManager`) built lazily off the Application. Every ViewModel is registered in one `appViewModelFactory` ([util/ViewModelFactory.kt](app/src/main/java/com/radriver/kannada/util/ViewModelFactory.kt)) — add new ViewModels there, wired from the container.

Layers under `com.radriver.kannada`:

- `ui/` — Compose screens (stateless) + ViewModels exposing `StateFlow`, collected with `collectAsStateWithLifecycle`. One package per feature (dashboard, flashcards, quiz) plus `components/`, `navigation/`, `theme/`.
- `data/local/` — Room: `WordEntity`, `WordDao`, `AppDatabase` (version 2, `MIGRATION_1_2`), `SeedData` (46 frozen v1 words) + `SeedDataV2` (200 words added in v2; 246 total). Seed lists are **append-only**; vocabulary added later needs a new DB version + migration that inserts only the new list.
- `data/prefs/` — `UserPrefs` DataStore for streak metrics and the shared level filter (`selectedLevel`, used by both flashcards and quiz).
- `data/repository/` — `WordRepository` (vocab + quiz/review builders, word of the day), `ProgressRepository` (dashboard flow, per-answer mastery/review updates).
- `domain/model/` — `Category`, `Level` (BEGINNER/INTERMEDIATE/ADVANCED, free selection), `QuizQuestion`/`QuizWord`/`QuizMode` (EN→KN, KN→EN, listening), `Mastery` (`MASTERY_THRESHOLD = 3` correct quiz answers to master a word; `REVIEW_INTERVAL_DAYS` Leitner intervals mirrored in the DAO's SQL `CASE`).

## Load-bearing design decisions

These are deliberate; don't "fix" them without understanding why:

- **DB seeding** uses raw SQL (`ContentValues` inserts) inside `RoomDatabase.Callback.onCreate` — it runs inside SQLiteOpenHelper's creation transaction, making the seed atomic with table creation and guaranteed complete before the first query returns. Don't convert it to a coroutine + DAO call. Every DAO method must stay `suspend` or return `Flow` (this is also what keeps seeding off the main thread).
- **Quiz state** in `QuizViewModel` is built imperatively by `startQuiz()` into a `MutableStateFlow` — deliberately *not* derived with `stateIn`, which would re-randomize the quiz whenever subscriber count bounces (rotation, tab switch).
- **`TtsManager` is app-scoped** (owned by `AppContainer`, application context) so rotation never tears down the engine. It tries `kn-IN` then `kn` and exposes `INITIALIZING / READY / UNAVAILABLE` via `StateFlow<TtsStatus>`; UI must handle `UNAVAILABLE` (hint + disabled speaker buttons), never assume a Kannada voice exists.
- **Bottom navigation** uses `popUpTo(startDestination){saveState}` + `launchSingleTop` + `restoreState` so each tab keeps its ViewModel — an in-progress quiz survives tab switches. Preserve this pattern when touching `KannadaNavGraph`.
- **Streak updates** in `UserPrefs` are atomic single-`edit` read-modify-write operations.
- **`java.time` on minSdk 24** works only via core-library desugaring (enabled in [app/build.gradle.kts](app/build.gradle.kts)) — keep it if touching compile options.
- **Quiz building** (`WordRepository.buildQuiz`) loads the pool once and assembles questions in memory; question words honor the level filter, distractors prefer same level+category, then same level, then the global pool, with sizes defensively clamped. Options are unique by **both** kannada and english text (reverse/listening modes render English options). Keep it resilient to degenerate pools (returns empty list below 4 candidates).
- **Migrations, not destructive fallback**: `AppDatabase` registers explicit `Migration`s and must never use `fallbackToDestructiveMigration` (it would wipe per-word progress). Entity `@ColumnInfo(defaultValue)` strings must stay byte-identical to the migration's SQL `DEFAULT` clauses, or Room crashes upgrades. Both seeding paths (onCreate + migrations) share the single `insertWord` helper — new columns go there too.
- **`Category` and `Level` enums are append-only** — they're stored by name via TypeConverters; renaming/removing an entry breaks `valueOf` on existing rows.

## Constraints

- The app must stay fully offline: no network permission, no analytics, no external APIs.
- minSdk 24, targetSdk/compileSdk 34, JVM target 1.8.
- **Never run `git commit`** — the owner reviews every line and makes all commits herself. Leave changes in the working tree (staged only if asked).
