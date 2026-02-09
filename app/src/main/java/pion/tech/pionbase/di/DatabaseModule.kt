package pion.tech.pionbase.di

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.dsl.module
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.data.database.AppDatabase
import pion.tech.pionbase.data.database.dao.DummyDAO

val databaseModule =
    module {
        single<AppDatabase> {
            val builder =
                Room
                    .databaseBuilder(
                        get(),
                        AppDatabase::class.java,
                        AppDatabase.DATABASE_NAME,
                    ).enableMultiInstanceInvalidation()
                    .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)

            // Allow destructive migration only in DEBUG builds for development
            // In production, add proper migrations using .addMigrations()
            if (BuildConfig.DEBUG) {
                builder.fallbackToDestructiveMigration()
            } else {
                // In production, fallback only on downgrade (safer than always destructive)
                builder.fallbackToDestructiveMigrationOnDowngrade()
            }

            builder.build()
        }

        single<DummyDAO> { get<AppDatabase>().dummyDAO() }
    }
