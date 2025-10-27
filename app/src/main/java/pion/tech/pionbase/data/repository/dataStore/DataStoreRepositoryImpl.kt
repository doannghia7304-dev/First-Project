package pion.tech.pionbase.data.repository.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.util.Result
import java.io.IOException
import javax.inject.Inject

class DataStoreRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : DataStoreRepository {
        private val isPremiumKey = booleanPreferencesKey("isPremiumKey")
        private val tokenKey = stringPreferencesKey("tokenKey")

        override fun getIsPremium(): Flow<Result<Boolean>> =
            dataStore.data
                .map { prefs ->
                    Result.Success(prefs[isPremiumKey] ?: false) as Result<Boolean>
                }.catch { exception ->
                    emit(Result.Error<Boolean>(exception) as Result<Boolean>)
                }

        override fun setIsPremium(isPremium: Boolean): Flow<Result<Boolean>> =
            flow<Result<Boolean>> {
                dataStore.edit {
                    it[isPremiumKey] = isPremium
                }
                emit(Result.Success(isPremium))
            }.catch {
                emit(Result.Error(it))
            }

        override fun getToken(): Flow<Result<String?>> =
            dataStore.data
                .map { prefs ->
                    Result.Success(prefs[tokenKey]) as Result<String?>
                }.catch { exception ->
                    emit(Result.Error<String?>(exception) as Result<String?>)
                }

        override fun setToken(token: String): Flow<Result<String>> =
            flow<Result<String>> {
                dataStore.edit {
                    it[tokenKey] = token
                }
                emit(Result.Success(token))
            }.catch {
                emit(Result.Error(it))
            }
    }
