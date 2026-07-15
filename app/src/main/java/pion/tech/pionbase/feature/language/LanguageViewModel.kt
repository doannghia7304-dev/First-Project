package pion.tech.pionbase.feature.language

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.language.LanguageUIModel
import pion.tech.pionbase.data.model.language.toPresentation
import pion.tech.pionbase.domain.usecase.language.GetLanguagesUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class LanguageViewModel(
    private val getLanguagesUseCase: GetLanguagesUseCase,
) : BaseViewModel<LanguageUiState, Nothing>(LanguageUiState()) {

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        setState { copy(languagesUiState = UiState.Loading) }
        handleApiCall(
            apiCall = { getLanguagesUseCase() },
            onSuccess = { dtoList ->
                val languages = dtoList.map { it.toPresentation() }
                setState {
                    copy(
                        languagesUiState = UiState.Success(languages)
                    )
                }
            },
            onError = { throwable ->
                setState {
                    copy(
                        languagesUiState = UiState.Error(throwable)
                    )
                }
            },
        )
    }

    fun selectLanguage(item: LanguageUIModel) {
        setState {
            copy(
                selectedLanguage = item,
            )
        }
    }

    fun getSelectedLanguage(): LanguageUIModel? = uiState.value.selectedLanguage
}

data class LanguageUiState(
    val languagesUiState: UiState<List<LanguageUIModel>> = UiState.None,
    val selectedLanguage: LanguageUIModel? = null,
)

fun LanguageUiState.getSelectedLanguageListUiState(): UiState<List<LanguageUIModel>> {
    return when (languagesUiState) {
        is UiState.Success -> {
            UiState.Success(languagesUiState.data.map {
                if (it.localeCode == selectedLanguage?.localeCode) {
                    it.copy(
                        isSelected = true,
                    )
                } else {
                    it.copy(
                        isSelected = false,
                    )
                }
            })
        }

        else -> languagesUiState
    }
}