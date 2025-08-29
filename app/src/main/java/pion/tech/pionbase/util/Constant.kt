package pion.tech.pionbase.util

import kotlinx.coroutines.flow.first
import pion.datlt.libads.utils.AdsConstant
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.Result

object Constant {
    var isPremium = false

    const val iapId = "removeads"
    var isRemoteConfigSuccess = false

    suspend fun isPremiumValue(dataStoreRepository: DataStoreRepository): Boolean {
        val dataStoreResult = dataStoreRepository.getIsPremium().first()
        val dataStoreIsPremium =
            when (dataStoreResult) {
                is Result.Success -> dataStoreResult.data
                else -> false
            }

        return isPremium || AdsConstant.isPremium || dataStoreIsPremium
    }
}
