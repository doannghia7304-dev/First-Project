package pion.tech.pionbase.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pion.tech.pionbase.data.remote.ApiInterface
import pion.tech.pionbase.data.repository.apiRepository.ApiRepository
import pion.tech.pionbase.data.repository.apiRepository.ApiRepositoryImpl
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepositoryImpl
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepository
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepositoryImpl
import pion.tech.pionbase.data.repository.languageRepository.LanguageRepository
import pion.tech.pionbase.data.repository.languageRepository.LanguageRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun providePreferencesRepository(dataStore: DataStore<Preferences>): DataStoreRepository = DataStoreRepositoryImpl(dataStore)

    @Provides
    @Singleton
    fun provideLanguageRepository(): LanguageRepository = LanguageRepositoryImpl()

    @Provides
    @Singleton
    fun provideApiRepository(apiInterface: ApiInterface): ApiRepository = ApiRepositoryImpl(apiInterface)

    @Provides
    @Singleton
    fun provideInstalledAppsRepository(
        @ApplicationContext context: Context,
    ): InstalledAppsRepository = InstalledAppsRepositoryImpl(context)
}
