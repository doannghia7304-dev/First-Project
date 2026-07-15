package pion.tech.pionbase.di

import org.koin.core.module.Module

val appModules: List<Module> =
    listOf(
        // Core system dependencies
        coreModule,
        // Data layer
        networkModule,
        databaseModule,
        repositoryModule,
        // Domain layer
        useCaseModule,
        // Platform services
        platformModule,
        // UI layer
        viewModelModule,
    )
