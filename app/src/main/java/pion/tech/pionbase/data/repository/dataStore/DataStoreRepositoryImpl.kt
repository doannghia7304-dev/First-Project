package pion.tech.pionbase.data.repository.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : BaseRepository(), DataStoreRepository {
    private val isPremiumKey = booleanPreferencesKey("isPremiumKey")
    private val tokenKey = stringPreferencesKey("tokenKey")
    private val isLanguageSelectedKey = booleanPreferencesKey("isLanguageSelectedKey")
    private val languageCodeKey = stringPreferencesKey("languageCodeKey")
    private val liveWallpaperPathKey = stringPreferencesKey("liveWallpaperPathKey")
    private val videoWallpaperPathKey = stringPreferencesKey("videoWallpaperPathKey")
    private val staticWallpaperPathKey = stringPreferencesKey("staticWallpaperPathKey")
    private val activeWallpaperTypeKey = intPreferencesKey("activeWallpaperTypeKey")
    private val isOnboardingCompletedKey = booleanPreferencesKey("isOnboardingCompletedKey")
    private val isCalendarOverlayEnabledKey = booleanPreferencesKey("isCalendarOverlayEnabledKey")
    private val calendarPositionKey = intPreferencesKey("calendarPositionKey")
    private val calendarFontColorKey = intPreferencesKey("calendarFontColorKey")

    override fun getIsPremium(): Flow<Result<Boolean>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[isPremiumKey] ?: false
        }

    override fun setIsPremium(isPremium: Boolean): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[isPremiumKey] = isPremium
            }
        }

    override fun getToken(): Flow<Result<String?>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[tokenKey]
        }

    override fun setToken(token: String): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[tokenKey] = token
            }
        }

    override fun getIsLanguageSelected(): Flow<Result<Boolean>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[isLanguageSelectedKey] ?: false
        }

    override fun setIsLanguageSelected(isSelected: Boolean): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[isLanguageSelectedKey] = isSelected
            }
        }

    override fun getLanguageCode(): Flow<Result<String?>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[languageCodeKey]
        }

    override fun setLanguageCode(code: String): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[languageCodeKey] = code
            }
        }

    override fun getLiveWallpaperPath(): Flow<Result<String?>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[liveWallpaperPathKey]
        }

    override fun setLiveWallpaperPath(path: String): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[liveWallpaperPathKey] = path
            }
        }

    override fun getVideoWallpaperPath(): Flow<Result<String?>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[videoWallpaperPathKey]
        }

    override fun setVideoWallpaperPath(path: String): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[videoWallpaperPathKey] = path
            }
        }

    override fun getStaticWallpaperPath(): Flow<Result<String?>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[staticWallpaperPathKey]
        }

    override fun setStaticWallpaperPath(path: String): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[staticWallpaperPathKey] = path
            }
        }

    override fun getActiveWallpaperType(): Flow<Result<Int>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[activeWallpaperTypeKey] ?: DataStoreRepository.WALLPAPER_TYPE_NONE
        }

    override fun setActiveWallpaperType(type: Int): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[activeWallpaperTypeKey] = type
            }
        }

    override fun getIsOnboardingCompleted(): Flow<Result<Boolean>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[isOnboardingCompletedKey] ?: false
        }

    override fun setIsOnboardingCompleted(isCompleted: Boolean): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[isOnboardingCompletedKey] = isCompleted
            }
        }

    override fun getIsCalendarOverlayEnabled(): Flow<Result<Boolean>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[isCalendarOverlayEnabledKey] ?: false
        }

    override fun setIsCalendarOverlayEnabled(isEnabled: Boolean): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[isCalendarOverlayEnabledKey] = isEnabled
            }
        }

    override fun getCalendarPosition(): Flow<Result<Int>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[calendarPositionKey] ?: 2
        }

    override fun setCalendarPosition(position: Int): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[calendarPositionKey] = position
            }
        }

    override fun getCalendarFontColor(): Flow<Result<Int>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[calendarFontColorKey] ?: -1
        }

    override fun setCalendarFontColor(color: Int): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[calendarFontColorKey] = color
            }
        }

    private val searchHistoryKey = androidx.datastore.preferences.core.stringPreferencesKey("search_history_key")

    override fun getSearchHistory(): Flow<Result<List<String>>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            val raw = prefs[searchHistoryKey] ?: ""
            if (raw.isBlank()) emptyList() else raw.split("|||")
        }

    override fun saveSearchQuery(query: String): Flow<Result<Unit>> =
        executeDataCall {
            if (query.isNotBlank()) {
                dataStore.edit { prefs ->
                    val raw = prefs[searchHistoryKey] ?: ""
                    val currentList = if (raw.isBlank()) emptyList() else raw.split("|||")
                    val updated = (listOf(query.trim()) + currentList.filter { it != query.trim() }).take(10)
                    prefs[searchHistoryKey] = updated.joinToString("|||")
                }
            }
        }

    override fun clearSearchHistory(): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it.remove(searchHistoryKey)
            }
        }

    private val darkModeKey = androidx.datastore.preferences.core.intPreferencesKey("dark_mode_key")

    override fun getDarkMode(): Flow<Result<Int>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[darkModeKey] ?: androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        }

    override fun setDarkMode(mode: Int): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[darkModeKey] = mode
            }
        }
}
