package pion.tech.pionbase.feature.onboard

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchDefault
import pion.tech.pionbase.domain.usecase.onboard.SetOnboardingCompletedUseCase
import pion.tech.pionbase.util.handleApiCall

sealed class OnboardEvent {
    data object NextPage : OnboardEvent()
    data object PreviousPage : OnboardEvent()
    data object IAPSkipBtnClicked : OnboardEvent()
    data object GoToHomeScreen : OnboardEvent()
}

class OnboardViewModel(
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase
) : BaseViewModel<Unit, OnboardEvent>(Unit) {
    fun nextPage() {
        launchDefault {
            setEvent(OnboardEvent.NextPage)
        }
    }

    fun previousPage() {
        launchDefault {
            setEvent(OnboardEvent.PreviousPage)
        }
    }

    fun onClickedOnboardIapSkipButton() {
        launchDefault {
            setEvent(OnboardEvent.IAPSkipBtnClicked)
        }
    }

    fun goToHomeScreen() {
        handleApiCall(
            apiCall = { setOnboardingCompletedUseCase(true) },
            onSuccess = {
                launchDefault {
                    setEvent(OnboardEvent.GoToHomeScreen)
                }
            }
        )
    }
}
