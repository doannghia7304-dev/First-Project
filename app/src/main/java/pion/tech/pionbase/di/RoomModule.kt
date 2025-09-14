package pion.tech.pionbase.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pion.tech.pionbase.data.database.AppDatabase
import pion.tech.pionbase.data.local.dao.DummyDAO
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME,
            ).fallbackToDestructiveMigration()
            .enableMultiInstanceInvalidation()
            .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
            .build()

    @Provides
    @Singleton
    fun provideDummyDao(db: AppDatabase): DummyDAO = db.dummyDAO()
}
