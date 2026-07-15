package pion.tech.pionbase.data.repository.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.util.Result

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : DataStoreRepository {
    private val isPremiumKey = booleanPreferencesKey("isPremiumKey")
    private val tokenKey = stringPreferencesKey("tokenKey")

    override fun getIsPremium(): Flow<Result<Boolean>> =
        dataStore.data
            .map<Preferences, Result<Boolean>> { prefs ->
                Result.Success(prefs[isPremiumKey] ?: false)
            }.catch { exception ->
                emit(Result.Error(exception))
            }

    override fun setIsPremium(isPremium: Boolean): Flow<Result<Unit>> =
        flow<Result<Unit>> {
            dataStore.edit {
                it[isPremiumKey] = isPremium
            }
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }

    override fun getToken(): Flow<Result<String?>> =
        dataStore.data
            .map<Preferences, Result<String?>> { prefs ->
                Result.Success(prefs[tokenKey])
            }.catch { exception ->
                emit(Result.Error(exception))
            }

    override fun setToken(token: String): Flow<Result<Unit>> =
        flow<Result<Unit>> {
            dataStore.edit {
                it[tokenKey] = token
            }
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
}
