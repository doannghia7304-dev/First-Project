package pion.tech.pionbase.feature.splash

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.domain.usecase.language.GetLanguageSelectedUseCase
import pion.tech.pionbase.util.handleApiCall

data class SplashUiState(
    val isLanguageSelected: Boolean? = null
)

class SplashViewModel(
    private val getLanguageSelectedUseCase: GetLanguageSelectedUseCase
) : BaseViewModel<SplashUiState, Nothing>(SplashUiState()) {

    init {
        checkLanguageSelection()
    }

    private fun checkLanguageSelection() {
        handleApiCall(
            apiCall = { getLanguageSelectedUseCase() },
            onSuccess = { isSelected ->
                setState { copy(isLanguageSelected = isSelected) }
            },
            onError = {
                setState { copy(isLanguageSelected = false) }
            }
        )
    }
}
