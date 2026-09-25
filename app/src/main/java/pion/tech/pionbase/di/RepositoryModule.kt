package pion.tech.pionbase.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pion.tech.pionbase.data.repository.apiRepository.ApiRepository
import pion.tech.pionbase.data.repository.apiRepository.ApiRepositoryImpl
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepositoryImpl
import pion.tech.pionbase.data.repository.favoriteRepository.FavoriteRepository
import pion.tech.pionbase.data.repository.favoriteRepository.FavoriteRepositoryImpl
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepository
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepositoryImpl
import pion.tech.pionbase.data.repository.languageRepository.LanguageRepository
import pion.tech.pionbase.data.repository.languageRepository.LanguageRepositoryImpl
import pion.tech.pionbase.data.repository.wallpaper.WallpaperRepository
import pion.tech.pionbase.data.repository.wallpaper.WallpaperRepositoryImpl
import pion.tech.pionbase.data.repository.weather.WeatherRepository
import pion.tech.pionbase.data.repository.weather.WeatherRepositoryImpl

val repositoryModule =
    module {
        singleOf(::DataStoreRepositoryImpl) bind DataStoreRepository::class
        singleOf(::LanguageRepositoryImpl) bind LanguageRepository::class
        singleOf(::ApiRepositoryImpl) bind ApiRepository::class
        singleOf(::InstalledAppsRepositoryImpl) bind InstalledAppsRepository::class
        singleOf(::WallpaperRepositoryImpl) bind WallpaperRepository::class
        singleOf(::FavoriteRepositoryImpl) bind FavoriteRepository::class
        singleOf(::WeatherRepositoryImpl) bind WeatherRepository::class
    }
