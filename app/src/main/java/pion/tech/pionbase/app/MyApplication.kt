package pion.tech.pionbase.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.khaipv.recovery.core.Recovery
import dagger.hilt.android.HiltAndroidApp
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.base.lifecycleCallback.ActivityLifecycleCallbacksImpl
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
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
}
