package pion.tech.pionbase.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.khaipv.recovery.core.Recovery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.base.lifecycleCallback.ActivityLifecycleCallbacksImpl
import pion.tech.pionbase.di.appModules
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(appModules)
        }
        setupRemoteConfig()
        
        val dataStoreRepo: pion.tech.pionbase.data.repository.dataStore.DataStoreRepository = get()
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            dataStoreRepo.getDarkMode().collect { result ->
                if (result is pion.tech.pionbase.util.Result.Success) {
                    AppCompatDelegate.setDefaultNightMode(result.data)
                }
            }
        }

        if (BuildConfig.DEBUG) {
            Recovery
                .getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity::class.java)
                .recoverEnabled(true)
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                .init(this)

            Timber.plant(Timber.DebugTree())
        }
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksImpl())
    }

    private fun setupRemoteConfig() {
        val remoteConfig: FirebaseRemoteConfig = get()
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds =
                    if (BuildConfig.DEBUG) {
                        REMOTE_CONFIG_FETCH_INTERVAL_DEBUG
                    } else {
                        REMOTE_CONFIG_FETCH_INTERVAL_RELEASE
                    }
            },
        )
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    companion object {
        private const val REMOTE_CONFIG_FETCH_INTERVAL_DEBUG = 30L
        private const val REMOTE_CONFIG_FETCH_INTERVAL_RELEASE = 3600L
    }
}
