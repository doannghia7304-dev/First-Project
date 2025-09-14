package pion.tech.pionbase.data.repository.remoteConfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import pion.tech.pionbase.data.model.remoteConfig.RemoteConfigDtoModel
import pion.tech.pionbase.util.Result
import timber.log.Timber
import kotlin.coroutines.resume

private const val TAG = "RemoteConfigRepositoryI"

class RemoteConfigRepositoryImpl(
    private val remoteConfig: FirebaseRemoteConfig,
) : RemoteConfigRepository {
    companion object {
        private const val TIMEOUT_MS = 7000L
    }

    // Cache for remote config data
    private val _cachedRemoteConfig = MutableStateFlow<RemoteConfigDtoModel?>(null)

    override fun fetchRemoteConfig(): Flow<Result<RemoteConfigDtoModel>> =
        flow<Result<RemoteConfigDtoModel>> {
            val data =
                withTimeoutOrNull(TIMEOUT_MS) { fetchRemoteConfigData() }
                    ?: getDefaultRemoteConfigData()

            // Update cache
            _cachedRemoteConfig.value = data
            emit(Result.Success(data))
        }.catch { e ->
            Timber.tag(TAG).d("fetchRemoteConfig flow error: $e")
            val defaultData = getDefaultRemoteConfigData()
            _cachedRemoteConfig.value = defaultData
            emit(Result.Success(defaultData))
        }.flowOn(Dispatchers.IO)

    override fun getCachedRemoteConfig(): Flow<Result<RemoteConfigDtoModel?>> =
        _cachedRemoteConfig
            .asStateFlow()
            .map<RemoteConfigDtoModel?, Result<RemoteConfigDtoModel?>> { data -> Result.Success(data) }
            .catch { exception -> emit(Result.Error<RemoteConfigDtoModel?>(exception)) }

    private suspend fun fetchRemoteConfigData(): RemoteConfigDtoModel =
        suspendCancellableCoroutine { cont ->
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                try {
                    val result = createRemoteConfigEntity(task.isSuccessful)
                    cont.resume(result)
                } catch (e: Exception) {
                    e.printStackTrace()
                    cont.resume(getDefaultRemoteConfigData())
                }
            }
        }

    private fun getDefaultRemoteConfigData(): RemoteConfigDtoModel = createRemoteConfigEntity(isRealData = false)

    private fun createRemoteConfigEntity(isRealData: Boolean): RemoteConfigDtoModel =
        RemoteConfigDtoModel(
            firebaseRemoteConfig = remoteConfig,
            isRealData = isRealData,
        )
}
