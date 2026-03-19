package pion.tech.pionbase.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import org.koin.dsl.module
import pion.tech.pionbase.util.dataStore

val coreModule =
    module {
        single<FirebaseRemoteConfig> { Firebase.remoteConfig }

        single<DataStore<Preferences>> { get<Application>().dataStore }
    }
