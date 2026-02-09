package pion.tech.pionbase.feature.home

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.data.model.installedApp.toPresentation
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepository
import pion.tech.pionbase.util.handleApiCall

class HomeViewModel(
    private val installedAppsRepository: InstalledAppsRepository,
) : BaseViewModel<HomeUiState, Nothing>(HomeUiState()) {

    init {
        getInstalledApps()
    }

    fun getInstalledApps() {
        setState { copy(isLoading = true, error = null) }

        handleApiCall(
            apiCall = { installedAppsRepository.getInstalledApps() },
            onSuccess = { dtoList ->
                val installedApps = dtoList.map { it.toPresentation() }
                setState { copy(isLoading = false, installedApps = installedApps, error = null) }
            },
            onError = { throwable ->
                setState { copy(isLoading = false, error = throwable) }
            },
        )
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val installedApps: List<InstalledAppUIModel>? = null,
    val error: Throwable? = null,
)
