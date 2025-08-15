package pion.tech.pionbase.feature.splash

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
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
