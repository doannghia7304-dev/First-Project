package pion.tech.pionbase.feature.language

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.language.LanguageUIModel
import pion.tech.pionbase.data.model.language.toPresentation
import pion.tech.pionbase.data.repository.languageRepository.LanguageRepository
import pion.tech.pionbase.util.handleApiCall
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel
@Inject
constructor(
    private val repository: LanguageRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        handleApiCall(
            apiCall = { repository.getLanguage() },
            onSuccess = { dtoList ->
                val languages = dtoList.map { it.toPresentation() }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        languages = languages,
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

    fun selectLanguage(item: LanguageUIModel) {
        val updatedList =
            _uiState.value.languages.map { language ->
                language.copy(isSelected = language.localeCode == item.localeCode)
            }

        _uiState.value =
            _uiState.value.copy(
                languages = updatedList,
                selectedLanguage = updatedList.firstOrNull { it.isSelected },
            )
    }

    fun getSelectedLanguage(): LanguageUIModel? = _uiState.value.selectedLanguage
}

data class LanguageUiState(
    val isLoading: Boolean = false,
    val languages: List<LanguageUIModel> = emptyList(),
    val selectedLanguage: LanguageUIModel? = null,
    val error: Throwable? = null,
)
