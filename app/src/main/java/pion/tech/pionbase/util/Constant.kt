package pion.tech.pionbase.util

import kotlinx.coroutines.flow.first
import pion.datlt.libads.utils.AdsConstant
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.Result

object Constant {
    var isPremium = false

    const val iapId = "removeads"
    var isRemoteConfigSuccess = false

}
