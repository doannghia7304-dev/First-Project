package pion.tech.pionbase.app

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.appCategory.AppCategoryUIModel
import pion.tech.pionbase.data.model.appCategory.toPresentation
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.data.model.template.toPresentation
import pion.tech.pionbase.data.repository.apiRepository.ApiRepository
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.handleApiCall
import javax.inject.Inject

@HiltViewModel
class CommonViewModel
@Inject
constructor(
    private val apiRepository: ApiRepository,
    private val dataStoreRepository: DataStoreRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(CommonUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Load app categories. Can be called from multiple screens (e.g. Language, Home).
     */
    fun getAppId() {
        // turn on loading for category state
        _uiState.value = _uiState.value.copy(
            categoryState = _uiState.value.categoryState.copy(
                isLoading = true,
                error = null,
            ),
        )

        handleApiCall(
            apiCall = { apiRepository.getAppCategory() },
            onSuccess = { data ->
                val categories = data.map { item -> item.toPresentation() }
                _uiState.value = _uiState.value.copy(
                    categoryState = _uiState.value.categoryState.copy(
                        isLoading = false,
                        categories = categories,
                        error = null,
                    ),
                )
            },
            onError = { throwable ->
                _uiState.value = _uiState.value.copy(
                    categoryState = _uiState.value.categoryState.copy(
                        isLoading = false,
                        error = throwable,
                    ),
                )
            },
        )
    }

    /**
     * Load templates for a specific category id.
     */
    fun getTemplate(categoryId: String) {
        // turn on loading for template state
        _uiState.value = _uiState.value.copy(
            templateState = _uiState.value.templateState.copy(
                isLoading = true,
                error = null,
            ),
        )

        handleApiCall(
            apiCall = { apiRepository.getTemplateData(categoryId) },
            onSuccess = { data ->
                val templates = data.map { item -> item.toPresentation() }
                _uiState.value = _uiState.value.copy(
                    templateState = _uiState.value.templateState.copy(
                        isLoading = false,
                        templates = templates,
                        error = null,
                    ),
                )
            },
            onError = { throwable ->
                _uiState.value = _uiState.value.copy(
                    templateState = _uiState.value.templateState.copy(
                        isLoading = false,
                        error = throwable,
                    ),
                )
            },
        )
    }

    /**
     * Helper for screens like Home that only care about Template category.
     * It tries to use existing categories if available.
     */
    fun loadTemplateFromTemplateCategoryName(name: String = "Template") {
        val categories = _uiState.value.categoryState.categories

        val templateCategoryId =
            categories?.firstOrNull { item -> item.name == name }?.id
        if (templateCategoryId != null) {
            getTemplate(templateCategoryId)
        }
    }

    init {
        getAppId()
    }
}

data class CategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<AppCategoryUIModel>? = null,
    val error: Throwable? = null,
)

data class TemplateUiState(
    val isLoading: Boolean = false,
    val templates: List<TemplateUIModel>? = null,
    val error: Throwable? = null,
)

data class CommonUiState(
    val categoryState: CategoryUiState = CategoryUiState(),
    val templateState: TemplateUiState = TemplateUiState(),
)
