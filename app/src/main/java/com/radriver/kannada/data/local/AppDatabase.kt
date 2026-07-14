package com.radriver.kannada.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.radriver.kannada.data.local.dao.WordDao
import com.radriver.kannada.data.local.entity.WordEntity

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
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
                    .build()
                    .also { instance = it }
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
                SeedData.words.forEach { word ->
                    val values = ContentValues().apply {
                        put("english", word.english)
                        put("kannada", word.kannada)
                        put("transliteration", word.transliteration)
                        put("category", word.category.name)
                        put("correctCount", word.correctCount)
                    }
                    db.insert("words", SQLiteDatabase.CONFLICT_ABORT, values)
                }
            }
        }
    }
}
