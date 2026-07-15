package pion.tech.pionbase.feature.home

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.data.model.installedApp.toPresentation
import pion.tech.pionbase.domain.usecase.home.GetInstalledAppsUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class HomeViewModel(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) : BaseViewModel<HomeUiState, Nothing>(HomeUiState()) {

    init {
        getInstalledApps()
    }

    fun getInstalledApps() {
        setState { copy(installedAppsUiState = UiState.Loading) }

        handleApiCall(
            apiCall = { getInstalledAppsUseCase() },
            onSuccess = { dtoList ->
                val installedApps = dtoList.map { it.toPresentation() }
                setState { copy(installedAppsUiState = UiState.Success(installedApps)) }
            },
            onError = { throwable ->
                setState { copy(installedAppsUiState = UiState.Error(throwable)) }
            },
        )
    }
}

data class HomeUiState(
    val installedAppsUiState: UiState<List<InstalledAppUIModel>> = UiState.None,
)
