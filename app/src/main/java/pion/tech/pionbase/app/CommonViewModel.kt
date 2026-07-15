package pion.tech.pionbase.app

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.handleApiCall

class CommonViewModel(
    private val dataStoreRepository: DataStoreRepository,
) : BaseViewModel<CommonUiState, Nothing>(CommonUiState()) {

    private fun getIsPremium() {
        handleApiCall(
            apiCall = { dataStoreRepository.getIsPremium() },
            onSuccess = { isPremium ->
                setState { copy(isPremium = isPremium) }
            },
        )
    }

    fun setPremium(isPremium: Boolean) {
        handleApiCall(apiCall = { dataStoreRepository.setIsPremium(isPremium) })
    }

    init {
        getIsPremium()
    }
}

data class CommonUiState(
    val isPremium: Boolean = false,
)
