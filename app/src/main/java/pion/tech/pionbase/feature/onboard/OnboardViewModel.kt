package pion.tech.pionbase.feature.onboard

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchDefault

sealed class OnboardEvent {
    data object NextPage : OnboardEvent()
    data object PreviousPage : OnboardEvent()
    data object IAPSkipBtnClicked: OnboardEvent()
    data object GoToHomeScreen: OnboardEvent()
}

class OnboardViewModel : BaseViewModel<Unit, OnboardEvent>(Unit) {
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
        launchDefault {
            setEvent(OnboardEvent.GoToHomeScreen)
        }
    }
}
