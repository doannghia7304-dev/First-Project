package pion.tech.pionbase.feature.splash

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.domain.usecase.language.GetLanguageSelectedUseCase
import pion.tech.pionbase.domain.usecase.onboard.GetOnboardingCompletedUseCase
import pion.tech.pionbase.util.handleApiCall

data class SplashUiState(
    val isLanguageSelected: Boolean? = null,
    val isOnboardingCompleted: Boolean? = null
)

class SplashViewModel(
    private val getLanguageSelectedUseCase: GetLanguageSelectedUseCase,
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase
) : BaseViewModel<SplashUiState, Nothing>(SplashUiState()) {

    init {
        checkStartDestination()
    }

    private fun checkStartDestination() {
        handleApiCall(
            apiCall = { getLanguageSelectedUseCase() },
            onSuccess = { isSelected ->
                setState { copy(isLanguageSelected = isSelected) }
            },
            onError = {
                setState { copy(isLanguageSelected = false) }
            }
        )

        handleApiCall(
            apiCall = { getOnboardingCompletedUseCase() },
            onSuccess = { isCompleted ->
                setState { copy(isOnboardingCompleted = isCompleted) }
            },
            onError = {
                setState { copy(isOnboardingCompleted = false) }
            }
        )
    }
}
