package pion.tech.pionbase.feature.splash

import dagger.hilt.android.lifecycle.HiltViewModel
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.Constant
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val dataStoreRepository: DataStoreRepository,
    ) : BaseViewModel() {
        suspend fun isPremium(): Boolean = Constant.isPremiumValue(dataStoreRepository)

        fun setPremium(isPremium: Boolean) {
            launchIO {
                dataStoreRepository.setIsPremium(isPremium)
            }
        }
    }
