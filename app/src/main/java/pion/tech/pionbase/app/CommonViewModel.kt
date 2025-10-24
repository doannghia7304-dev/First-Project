package pion.tech.pionbase.app

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.data.model.appCategory.AppCategoryUIModel
import pion.tech.pionbase.data.model.appCategory.toPresentation
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.data.model.template.toPresentation
import pion.tech.pionbase.data.repository.apiRepository.ApiRepository
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import javax.inject.Inject

@HiltViewModel
class CommonViewModel
    @Inject
    constructor(
        private val apiRepository: ApiRepository,
        private val dataStoreRepository: DataStoreRepository,
    ) : BaseViewModel() {
        private val _getCategoryUiState =
            MutableStateFlow<UiState<List<AppCategoryUIModel>>>(UiState.None)
        val getCategoryUiState = _getCategoryUiState.asStateFlow()

        private val _getTemplateUiState =
            MutableStateFlow<UiState<List<TemplateUIModel>>>(UiState.None)
        val getTemplateUiState = _getTemplateUiState.asStateFlow()

        private fun getAppId() {
            handleApiCall(
                stateFlow = _getCategoryUiState,
                apiCall = { apiRepository.getAppCategory() },
                transform = { data -> data.map { item -> item.toPresentation() } },
            )
        }

        fun getTemplate(categoryId: String) {
            handleApiCall(
                stateFlow = _getTemplateUiState,
                apiCall = { apiRepository.getTemplateData(categoryId) },
                transform = { data -> data.map { item -> item.toPresentation() } },
            )
        }

        init {
            getAppId()
        }
    }
