package pion.tech.pionbase.di

import org.koin.core.module.Module
import pion.tech.pionbase.di.core.coreModule
import pion.tech.pionbase.di.data.local.databaseModule
import pion.tech.pionbase.di.data.network.networkModule
import pion.tech.pionbase.di.data.repository.repositoryModule
import pion.tech.pionbase.di.feature.viewModelModule
import pion.tech.pionbase.di.platform.platformModule

/**
 * Central DI module loader
 * All Koin modules are loaded from here in dependency order:
 * 1. Core (system-level dependencies)
 * 2. Data (network, local database, repositories)
 * 3. Platform (platform services like Firebase)
 * 4. Feature (UI layer ViewModels)
 */
val appModules: List<Module> =
    listOf(
        // Core system dependencies
        coreModule,
        // Data layer
        networkModule,
        databaseModule,
        repositoryModule,
        // Platform services
        platformModule,
        // UI layer
        viewModelModule,
    )
