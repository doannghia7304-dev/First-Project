package pion.tech.pionbase.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import pion.tech.pionbase.BuildConfig

/**
 * DataStore extension property for Context.
 * This provides a singleton DataStore instance per application process.
 *
 * Usage in DI:
 * ```kotlin
 * single<DataStore<Preferences>> { get<Application>().dataStore }
 * ```
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "${BuildConfig.APPLICATION_ID}_preferences",
)
