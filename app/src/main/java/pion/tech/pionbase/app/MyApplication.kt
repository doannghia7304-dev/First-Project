package pion.tech.pionbase.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.khaipv.recovery.core.Recovery
import io.kotzilla.sdk.analytics.koin.analytics
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

        // Start Koin
        startKoin {
            androidContext(this@MyApplication)
            analytics()
            modules(appModules)
        }

        // Initialize Firebase Remote Config
        setupRemoteConfig()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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

            Timber.Forest.plant(Timber.DebugTree())
        }
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksImpl())
    }

    private fun setupRemoteConfig() {
        val remoteConfig: FirebaseRemoteConfig = get()
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds =
                    if (BuildConfig.DEBUG) {
                        30
                    } else {
                        3600
                    }
            },
        )
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }
}
