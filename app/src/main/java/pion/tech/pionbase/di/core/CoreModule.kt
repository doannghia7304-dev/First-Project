package pion.tech.pionbase.di.core

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import org.koin.dsl.module
import pion.tech.pionbase.util.dataStore

/**
 * Core application dependencies
 * - Firebase Remote Config
 * - DataStore (initialized in Application, this just provides the instance)
 */
val coreModule =
    module {
        single<FirebaseRemoteConfig> { Firebase.remoteConfig }

        single<DataStore<Preferences>> { get<Application>().dataStore }
    }
