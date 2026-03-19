package pion.tech.pionbase.data.repository.dataStore

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.util.Result

interface DataStoreRepository {
    fun getIsPremium(): Flow<Result<Boolean>>

    fun setIsPremium(isPremium: Boolean): Flow<Result<Unit>>

    fun getToken(): Flow<Result<String?>>

    fun setToken(token: String): Flow<Result<Unit>>
}
