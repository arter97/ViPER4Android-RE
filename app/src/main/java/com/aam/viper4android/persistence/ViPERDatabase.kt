package com.aam.viper4android.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aam.viper4android.persistence.converter.InstantConverter
import com.aam.viper4android.persistence.model.PersistedPreset
import com.aam.viper4android.persistence.model.PersistedSession
import com.aam.viper4android.persistence.model.PersistedSetting
import java.time.Instant

@Database(
    entities = [PersistedSetting::class, PersistedSession::class, PersistedPreset::class],
    version = 3,
)
@TypeConverters(
    InstantConverter::class,
)
abstract class ViPERDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun sessionsDao(): SessionDao
    abstract fun presetsDao(): PresetsDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE sessions ADD COLUMN started_at INTEGER NOT NULL DEFAULT 0",
                )

                val now = Instant.now()
                db.execSQL(
                    "UPDATE sessions SET started_at = ${now.toEpochMilli()}",
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE presets ADD COLUMN legacy_mode INTEGER NOT NULL DEFAULT 0",
                )
            }
        }

        fun getInstance(context: Context): ViPERDatabase {
            return Room.databaseBuilder(context, ViPERDatabase::class.java, "ViPERDatabase")
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                )
                .build()
        }
    }
}