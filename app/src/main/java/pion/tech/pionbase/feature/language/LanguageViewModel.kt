package pion.tech.pionbase.feature.language

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.language.LanguageUIModel
import pion.tech.pionbase.data.model.language.toPresentation
import pion.tech.pionbase.domain.usecase.language.GetLanguageCodeUseCase
import pion.tech.pionbase.domain.usecase.language.GetLanguagesUseCase
import pion.tech.pionbase.domain.usecase.language.SetLanguageCodeUseCase
import pion.tech.pionbase.domain.usecase.language.SetLanguageSelectedUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class LanguageViewModel(
    private val getLanguagesUseCase: GetLanguagesUseCase,
    private val setLanguageSelectedUseCase: SetLanguageSelectedUseCase,
    private val getLanguageCodeUseCase: GetLanguageCodeUseCase,
    private val setLanguageCodeUseCase: SetLanguageCodeUseCase,
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
                handleApiCall(
                    apiCall = { getLanguageCodeUseCase() },
                    onSuccess = { savedCode ->
                        // Mặc định chọn English ("en") nếu chưa có ngôn ngữ nào được lưu trước đó
                        val defaultCode = if (!savedCode.isNullOrEmpty()) savedCode else "en"
                        val defaultItem = languages.firstOrNull { it.localeCode == defaultCode } ?: languages.firstOrNull()
                        setState {
                            copy(
                                languagesUiState = UiState.Success(languages),
                                selectedLanguage = defaultItem
                            )
                        }
                    },
                    onError = {
                        val defaultEnglish = languages.firstOrNull { it.localeCode == "en" } ?: languages.firstOrNull()
                        setState {
                            copy(
                                languagesUiState = UiState.Success(languages),
                                selectedLanguage = defaultEnglish
                            )
                        }
                    }
                )
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

    fun saveLanguageSelected() {
        val selectedCode = uiState.value.selectedLanguage?.localeCode ?: "en"
        handleApiCall(apiCall = { setLanguageSelectedUseCase(true) })
        handleApiCall(apiCall = { setLanguageCodeUseCase(selectedCode) })
    }

    fun getSelectedLanguage(): LanguageUIModel? = uiState.value.selectedLanguage
}

data class LanguageUiState(
    val languagesUiState: UiState<List<LanguageUIModel>> = UiState.None,
    val selectedLanguage: LanguageUIModel? = null,
)

fun LanguageUiState.getSelectedLanguageListUiState(): UiState<List<LanguageUIModel>> {
    // 1. Dùng biến trung gian để smart cast và tránh gọi getter nhiều lần
    return when (val state = languagesUiState) {
        is UiState.Success -> {
            // 2. Đưa phép tính không đổi ra ngoài vòng lặp
            val targetLocaleCode = selectedLanguage?.localeCode

            UiState.Success(state.data.map { item ->
                // 3. Gán trực tiếp kết quả biểu thức logic
                val shouldBeSelected = (item.localeCode == targetLocaleCode)

                // 4. Chỉ copy (tạo object mới) khi trạng thái thực sự thay đổi
                if (item.isSelected == shouldBeSelected) {
                    item
                } else {
                    item.copy(isSelected = shouldBeSelected)
                }
            })
        }

        else -> state
    }
}