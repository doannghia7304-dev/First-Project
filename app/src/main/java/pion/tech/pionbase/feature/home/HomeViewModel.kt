package pion.tech.pionbase.feature.home

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.installedApp.InstalledAppDtoModel
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.data.model.installedApp.toPresentation
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepository
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val dataStoreRepository: DataStoreRepository,
        private val installedAppsRepository: InstalledAppsRepository,
    ) : BaseViewModel() {
        private val _installedAppsUiState =
            MutableStateFlow<UiState<List<InstalledAppUIModel>>>(UiState.None)
        val installedAppsUiState = _installedAppsUiState.asStateFlow()

        suspend fun getIsPremiumValue(): Flow<Result<Boolean>> = dataStoreRepository.getIsPremium()

        init {
            getInstalledApps()
        }

        fun getInstalledApps() {
            handleApiCall(
                stateFlow = _installedAppsUiState,
                apiCall = { installedAppsRepository.getInstalledApps() },
                transform = { dtoList: List<InstalledAppDtoModel> ->
                    dtoList.map { it.toPresentation() }
                },
            )
        }
    }
