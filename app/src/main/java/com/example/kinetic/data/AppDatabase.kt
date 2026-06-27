package com.example.kinetic.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Profile::class, ProfileSettings::class, Exercise::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS exercises (
                        id TEXT NOT NULL PRIMARY KEY,
                        profileId INTEGER NOT NULL,
                        workoutId TEXT NOT NULL,
                        weight REAL NOT NULL DEFAULT 0.0,
                        weightUnit TEXT NOT NULL DEFAULT 'KG',
                        FOREIGN KEY(profileId) REFERENCES profiles(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_exercises_profileId ON exercises(profileId)"
                )
                db.execSQL(
                    "ALTER TABLE profile_settings ADD COLUMN isHighContrastEnabled INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "ALTER TABLE profile_settings ADD COLUMN isCardLayout INTEGER NOT NULL DEFAULT 1"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kinetic_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
