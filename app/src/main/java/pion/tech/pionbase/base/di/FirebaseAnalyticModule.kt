package pion.tech.pionbase.base.di

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseAnalyticsLogger
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseAnalyticsLoggerImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseAnalyticsModule {
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(application: Application): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(application)
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalyticsLogger(firebaseAnalytics: FirebaseAnalytics): FirebaseAnalyticsLogger {
        return FirebaseAnalyticsLoggerImpl(firebaseAnalytics)
    }
}