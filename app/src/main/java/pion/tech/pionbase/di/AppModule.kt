package pion.tech.pionbase.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.piontech.core.lifecycleCallback.FragmentLifecycleAction
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.FragmentLifecycleActionImpl
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "${BuildConfig.APPLICATION_ID}_preferences")

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig =
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds =
                        if (BuildConfig.DEBUG) {
                            30
                        } else {
                            3600
                        }
                },
            )
            setDefaultsAsync(R.xml.remote_config_defaults)
        }

    @Provides
    @Singleton
    fun provideDataStore(application: Application): DataStore<Preferences> = application.dataStore

    @Provides
    fun provideFragmentLifecycleAction(dataStoreRepository: DataStoreRepository): FragmentLifecycleAction =
        FragmentLifecycleActionImpl(dataStoreRepository)
}
