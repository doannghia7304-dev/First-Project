package pion.tech.pionbase.feature.language

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.language.LanguageUIModel
import pion.tech.pionbase.data.model.language.toPresentation
import pion.tech.pionbase.data.repository.languageRepository.LanguageRepository
import pion.tech.pionbase.util.handleApiCall

class LanguageViewModel(
    private val repository: LanguageRepository,
) : BaseViewModel<LanguageUiState, Nothing>(LanguageUiState()) {

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        setState { copy(isLoading = true, error = null) }

        handleApiCall(
            apiCall = { repository.getLanguage() },
            onSuccess = { dtoList ->
                val languages = dtoList.map { it.toPresentation() }
                setState { copy(isLoading = false, languages = languages, error = null) }
            },
            onError = { throwable ->
                setState { copy(isLoading = false, error = throwable) }
            },
        )
    }

    fun selectLanguage(item: LanguageUIModel) {
        val current = uiState.value.languages ?: return
        val updatedList =
            current.map { language ->
                language.copy(isSelected = language.localeCode == item.localeCode)
            }

        setState {
            copy(
                languages = updatedList,
                selectedLanguage = updatedList.firstOrNull { it.isSelected },
            )
        }
    }

    fun getSelectedLanguage(): LanguageUIModel? = uiState.value.selectedLanguage
}

data class LanguageUiState(
    val isLoading: Boolean = false,
    val languages: List<LanguageUIModel>? = null,
    val selectedLanguage: LanguageUIModel? = null,
    val error: Throwable? = null,
)
