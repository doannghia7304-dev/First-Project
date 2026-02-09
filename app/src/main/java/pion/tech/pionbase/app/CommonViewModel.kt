package pion.tech.pionbase.app

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.appCategory.AppCategoryUIModel
import pion.tech.pionbase.data.model.appCategory.toPresentation
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.data.model.template.toPresentation
import pion.tech.pionbase.data.repository.apiRepository.ApiRepository
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.handleApiCall

class CommonViewModel(
    private val apiRepository: ApiRepository,
    private val dataStoreRepository: DataStoreRepository,
) : BaseViewModel<CommonUiState, Nothing>(CommonUiState()) {
    /**
     * Load app categories. Can be called from multiple screens (e.g. Language, Home).
     */
    fun getAppId() {
        setState {
            copy(categoryState = categoryState.copy(isLoading = true, error = null))
        }

        handleApiCall(
            apiCall = { apiRepository.getAppCategory() },
            onSuccess = { data ->
                val categories = data.map { item -> item.toPresentation() }
                setState {
                    copy(
                        categoryState =
                            categoryState.copy(
                                isLoading = false,
                                categories = categories,
                                error = null,
                            ),
                    )
                }
            },
            onError = { throwable ->
                setState {
                    copy(categoryState = categoryState.copy(isLoading = false, error = throwable))
                }
            },
        )
    }

    /**
     * Load templates for a specific category id.
     */
    fun getTemplate(categoryId: String) {
        setState {
            copy(templateState = templateState.copy(isLoading = true, error = null))
        }

        handleApiCall(
            apiCall = { apiRepository.getTemplateData(categoryId) },
            onSuccess = { data ->
                val templates = data.map { item -> item.toPresentation() }
                setState {
                    copy(
                        templateState =
                            templateState.copy(
                                isLoading = false,
                                templates = templates,
                                error = null,
                            ),
                    )
                }
            },
            onError = { throwable ->
                setState {
                    copy(templateState = templateState.copy(isLoading = false, error = throwable))
                }
            },
        )
    }

    /**
     * Helper for screens like Home that only care about Template category.
     * It tries to use existing categories if available.
     */
    fun loadTemplateFromTemplateCategoryName(name: String = "Template") {
        val categories = uiState.value.categoryState.categories

        val templateCategoryId =
            categories?.firstOrNull { item -> item.name == name }?.id
        if (templateCategoryId != null) {
            getTemplate(templateCategoryId)
        }
    }

    private fun getIsPremium() {
        handleApiCall(
            apiCall = { dataStoreRepository.getIsPremium() },
            onSuccess = { isPremium ->
                setState { copy(isPremium = isPremium) }
            },
        )
    }

    fun setPremium(isPremium: Boolean) {
        handleApiCall(apiCall = { dataStoreRepository.setIsPremium(isPremium) })
    }

    init {
        getIsPremium()
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
    val isPremium: Boolean = false,
)
