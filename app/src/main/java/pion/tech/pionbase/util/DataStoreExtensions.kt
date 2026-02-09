package pion.tech.pionbase.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import pion.tech.pionbase.BuildConfig

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "${BuildConfig.APPLICATION_ID}_preferences",
)
