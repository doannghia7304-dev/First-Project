package pion.tech.pionbase.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pion.tech.pionbase.domain.usecase.home.GetInstalledAppsUseCase
import pion.tech.pionbase.domain.usecase.language.GetLanguagesUseCase

val homeUseCaseModule = module {
    factoryOf(::GetInstalledAppsUseCase)
}

val languageUseCaseModule = module {
    factoryOf(::GetLanguagesUseCase)
}

val useCaseModule = module {
    includes(
        homeUseCaseModule,
        languageUseCaseModule
    )
}
