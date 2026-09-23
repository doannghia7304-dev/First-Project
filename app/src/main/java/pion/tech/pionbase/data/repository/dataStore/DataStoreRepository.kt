package pion.tech.pionbase.data.repository.dataStore

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.util.Result

interface DataStoreRepository {
    fun getIsPremium(): Flow<Result<Boolean>>

    fun setIsPremium(isPremium: Boolean): Flow<Result<Unit>>

    fun getToken(): Flow<Result<String?>>

    fun setToken(token: String): Flow<Result<Unit>>

    fun getIsLanguageSelected(): Flow<Result<Boolean>>

    fun setIsLanguageSelected(isSelected: Boolean): Flow<Result<Unit>>

    fun getLanguageCode(): Flow<Result<String?>>

    fun setLanguageCode(code: String): Flow<Result<Unit>>

    fun getLiveWallpaperPath(): Flow<Result<String?>>

    fun setLiveWallpaperPath(path: String): Flow<Result<Unit>>

    fun getVideoWallpaperPath(): Flow<Result<String?>>

    fun setVideoWallpaperPath(path: String): Flow<Result<Unit>>

    fun getStaticWallpaperPath(): Flow<Result<String?>>

    fun setStaticWallpaperPath(path: String): Flow<Result<Unit>>

    fun getActiveWallpaperType(): Flow<Result<Int>>

    fun setActiveWallpaperType(type: Int): Flow<Result<Unit>>

    fun getIsOnboardingCompleted(): Flow<Result<Boolean>>

    fun setIsOnboardingCompleted(isCompleted: Boolean): Flow<Result<Unit>>

    fun getIsCalendarOverlayEnabled(): Flow<Result<Boolean>>

    fun setIsCalendarOverlayEnabled(isEnabled: Boolean): Flow<Result<Unit>>

    fun getCalendarPosition(): Flow<Result<Int>>

    fun setCalendarPosition(position: Int): Flow<Result<Unit>>

    fun getCalendarFontColor(): Flow<Result<Int>>

    fun setCalendarFontColor(color: Int): Flow<Result<Unit>>

    fun getSearchHistory(): Flow<Result<List<String>>>

    fun saveSearchQuery(query: String): Flow<Result<Unit>>

    fun clearSearchHistory(): Flow<Result<Unit>>

    fun getDarkMode(): Flow<Result<Int>>

    fun setDarkMode(mode: Int): Flow<Result<Unit>>

    companion object {
        const val WALLPAPER_TYPE_NONE = 0
        const val WALLPAPER_TYPE_STATIC = 1
        const val WALLPAPER_TYPE_GIF = 2
        const val WALLPAPER_TYPE_VIDEO = 3
    }
}
