package pion.tech.pionbase.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(impl: DataStoreRepositoryImpl): DataStoreRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(impl: LanguageRepositoryImpl): LanguageRepository

    @Binds
    @Singleton
    abstract fun bindApiRepository(impl: ApiRepositoryImpl): ApiRepository

    @Binds
    @Singleton
    abstract fun bindInstalledAppsRepository(impl: InstalledAppsRepositoryImpl): InstalledAppsRepository
}
