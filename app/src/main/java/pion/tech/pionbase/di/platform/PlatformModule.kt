package pion.tech.pionbase.di.platform

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseAnalyticsLogger
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseAnalyticsLoggerImpl

/**
 * Platform-specific dependencies
 * - Firebase Analytics
 * - Other platform services (Crashlytics, Performance, etc.)
 */
val platformModule =
    module {
        // FirebaseAnalytics - static factory, cannot use singleOf
        single<FirebaseAnalytics> { FirebaseAnalytics.getInstance(get<Application>()) }

        // FirebaseAnalyticsLogger - simple constructor, use singleOf (best practice)
        singleOf(::FirebaseAnalyticsLoggerImpl) bind FirebaseAnalyticsLogger::class
    }
