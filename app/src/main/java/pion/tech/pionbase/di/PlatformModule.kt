package pion.tech.pionbase.di

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseAnalyticsLogger
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseAnalyticsLoggerImpl

val platformModule =
    module {
        single<FirebaseAnalytics> { FirebaseAnalytics.getInstance(get<Application>()) }
        singleOf(::FirebaseAnalyticsLoggerImpl) bind FirebaseAnalyticsLogger::class
    }
