package pion.tech.pionbase.feature.home

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.data.model.installedApp.toPresentation
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepository
import pion.tech.pionbase.util.handleApiCall
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val installedAppsRepository: InstalledAppsRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getInstalledApps()
    }

    fun getInstalledApps() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        handleApiCall(
            apiCall = { installedAppsRepository.getInstalledApps() },
            onSuccess = { dtoList ->
                val installedApps = dtoList.map { it.toPresentation() }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        installedApps = installedApps,
                        error = null,
                    )
            },
            onError = { throwable ->
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = throwable,
                    )
            },
        )
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val installedApps: List<InstalledAppUIModel> = emptyList(),
    val error: Throwable? = null,
)
