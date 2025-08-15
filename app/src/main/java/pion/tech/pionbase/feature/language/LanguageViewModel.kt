package pion.tech.pionbase.feature.language

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.data.model.installedApp.InstalledAppDtoModel
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.data.model.installedApp.toPresentation
import pion.tech.pionbase.data.model.language.LanguageDtoModel
import pion.tech.pionbase.data.model.language.LanguageUIModel
import pion.tech.pionbase.data.model.language.toPresentation
import pion.tech.pionbase.data.repository.languageRepository.LanguageRepository
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall
import pion.tech.pionbase.util.onSuccess
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel
    @Inject
    constructor(
        private val repository: LanguageRepository,
    ) : BaseViewModel() {
        private val _languageData = MutableStateFlow<UiState<List<LanguageUIModel>>>(UiState.None)
        val languageData = _languageData.asStateFlow()

        init {
            loadLanguages()
        }

        private fun loadLanguages() {
            handleApiCall(
                stateFlow = _languageData,
                apiCall = { repository.getLanguage() },
                transform = {
                    it.map { item -> item.toPresentation() }
                },
            )
        }

        fun selectLanguage(item: LanguageUIModel) {
            val currentState = _languageData.value
            if (currentState is UiState.Success) {
                val updatedList =
                    currentState.data.map { language ->
                        language.copy(isSelected = language.localeCode == item.localeCode)
                    }
                _languageData.value = UiState.Success(updatedList)
            }
        }

        fun getSelectedLanguage(): LanguageUIModel? {
            val currentState = _languageData.value
            return if (currentState is UiState.Success) {
                currentState.data.firstOrNull { it.isSelected }
            } else {
                null
            }
        }
    }
