package pion.tech.pionbase.util

import kotlinx.coroutines.flow.first
import pion.datlt.libads.utils.AdsConstant
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.Result

object Constant {
    var isPremium = false

    const val iapId = "removeads"
    var isRemoteConfigSuccess = false

    suspend fun setPremium(
        isPremium: Boolean,
        dataStoreRepository: DataStoreRepository,
    ) {
        dataStoreRepository.setIsPremium(isPremium).first()
    }

    suspend fun isPremiumValue(dataStoreRepository: DataStoreRepository): Boolean =
        (
                dataStoreRepository
                    .getIsPremium()
                    .first()
                    .asSuccessOrNull() ?: false || AdsConstant.isPremium || isPremium
                )
}
