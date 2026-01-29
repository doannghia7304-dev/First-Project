package pion.tech.pionbase.di.feature

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.feature.home.HomeViewModel
import pion.tech.pionbase.feature.language.LanguageViewModel
import pion.tech.pionbase.feature.onboard.OnboardViewModel
import pion.tech.pionbase.feature.setting.SettingViewModel
import pion.tech.pionbase.feature.splash.SplashViewModel

/**
 * Presentation layer dependencies
 * ViewModels for each feature
 */
val viewModelModule =
    module {
        viewModelOf(::CommonViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::LanguageViewModel)
        viewModelOf(::SplashViewModel)
        viewModelOf(::SettingViewModel)
        viewModelOf(::OnboardViewModel)
    }
