package com.radriver.kannada.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.radriver.kannada.data.local.dao.WordDao
import com.radriver.kannada.data.local.entity.WordEntity

@Database(entities = [WordEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        private const val DB_NAME = "kannada_learn.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(SeedCallback)
                    // Never fall back to destructive migration: it would wipe
                    // the user's per-word progress (correctCount/wrongCount).
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }

        /**
         * Single raw-insert path shared by [SeedCallback] and [MIGRATION_1_2]
         * so the two call sites can never drift on columns. Any new column
         * must be added here AND to the entity AND to the migration that
         * introduces it.
         */
        private fun insertWord(db: SupportSQLiteDatabase, word: WordEntity) {
            val values = ContentValues().apply {
                put("english", word.english)
                put("kannada", word.kannada)
                put("transliteration", word.transliteration)
                put("category", word.category.name)
                put("level", word.level.name)
                put("correctCount", word.correctCount)
                put("wrongCount", word.wrongCount)
                // lastAnsweredEpochDay intentionally omitted -> NULL (never answered)
            }
            db.insert("words", SQLiteDatabase.CONFLICT_ABORT, values)
        }

        /**
         * Seeds the starter vocabulary synchronously inside onCreate.
         *
         * Why synchronous raw inserts instead of launching a coroutine with a DAO:
         *  - onCreate runs inside SQLiteOpenHelper's creation transaction, so the
         *    seed is atomic with table creation — a process death mid-seed rolls
         *    everything back and onCreate simply runs again next launch.
         *  - The first query that triggers database creation always comes from a
         *    coroutine on a background dispatcher (every DAO method is suspend/Flow),
         *    so this never runs on the main thread.
         *  - It guarantees data is present before the very first read returns,
         *    eliminating the first-launch race where a quiz could see an empty table.
         */
        private object SeedCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                SeedData.words.forEach { insertWord(db, it) }
            }
        }

        /**
         * v1 -> v2: difficulty levels + review tracking + the v2 vocabulary.
         *
         * The DEFAULT clauses must stay byte-identical to the entity's
         * @ColumnInfo(defaultValue) annotations — Room validates the live
         * schema against them and crashes upgrades on any mismatch.
         * `DEFAULT 'BEGINNER'` is also the level backfill: every v1 word is
         * BEGINNER by design, so no UPDATE statements are needed.
         *
         * Room runs migrations inside a transaction, so a crash mid-migration
         * rolls back cleanly and it reruns on next launch — same atomicity
         * philosophy as the onCreate seed.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN level TEXT NOT NULL DEFAULT 'BEGINNER'")
                db.execSQL("ALTER TABLE words ADD COLUMN wrongCount INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE words ADD COLUMN lastAnsweredEpochDay INTEGER")
                SeedData.v2Words.forEach { insertWord(db, it) }
            }
        }
    }
}
