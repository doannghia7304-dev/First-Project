package pion.tech.pionbase.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pion.tech.pionbase.domain.usecase.home.GetInstalledAppsUseCase
import pion.tech.pionbase.domain.usecase.language.GetLanguagesUseCase
import pion.tech.pionbase.domain.usecase.language.GetLanguageSelectedUseCase
import pion.tech.pionbase.domain.usecase.language.SetLanguageSelectedUseCase
import pion.tech.pionbase.domain.usecase.onboard.GetOnboardingCompletedUseCase
import pion.tech.pionbase.domain.usecase.onboard.SetOnboardingCompletedUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetLiveWallpaperPathUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetVideoWallpaperPathUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SaveGifWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SaveVideoWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SetLiveWallpaperPathUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SetVideoWallpaperPathUseCase

val homeUseCaseModule = module {
    factoryOf(::GetInstalledAppsUseCase)
}

val wallpaperUseCaseModule = module {
    factoryOf(::GetLiveWallpaperPathUseCase)
    factoryOf(::SetLiveWallpaperPathUseCase)
    factoryOf(::SaveGifWallpaperUseCase)
    factoryOf(::GetVideoWallpaperPathUseCase)
    factoryOf(::SetVideoWallpaperPathUseCase)
    factoryOf(::SaveVideoWallpaperUseCase)
}

val languageUseCaseModule = module {
    factoryOf(::GetLanguagesUseCase)
    factoryOf(::GetLanguageSelectedUseCase)
    factoryOf(::SetLanguageSelectedUseCase)
}

val onboardUseCaseModule = module {
    factoryOf(::GetOnboardingCompletedUseCase)
    factoryOf(::SetOnboardingCompletedUseCase)
}

val useCaseModule = module {
    includes(
        homeUseCaseModule,
        wallpaperUseCaseModule,
        languageUseCaseModule,
        onboardUseCaseModule
    )
}
