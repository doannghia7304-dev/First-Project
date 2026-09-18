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
}