package pion.tech.pionbase.feature.setting

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.domain.usecase.wallpaper.*
import pion.tech.pionbase.util.handleApiCall

data class SettingUiState(
    val isCalendarOverlayEnabled: Boolean = false,
    val calendarPosition: Int = 2, // 0: Top, 1: Center, 2: Bottom
    val calendarFontColor: Int = -1 // Default white
)

class SettingViewModel(
    private val getCalendarOverlayEnabledUseCase: GetCalendarOverlayEnabledUseCase,
    private val setCalendarOverlayEnabledUseCase: SetCalendarOverlayEnabledUseCase,
    private val getCalendarPositionUseCase: GetCalendarPositionUseCase,
    private val setCalendarPositionUseCase: SetCalendarPositionUseCase,
    private val getCalendarFontColorUseCase: GetCalendarFontColorUseCase,
    private val setCalendarFontColorUseCase: SetCalendarFontColorUseCase
) : BaseViewModel<SettingUiState, Nothing>(SettingUiState()) {

    init {
        loadSettings()
    }

    private fun loadSettings() {
        handleApiCall(
            apiCall = { getCalendarOverlayEnabledUseCase() },
            onSuccess = { isEnabled -> setState { copy(isCalendarOverlayEnabled = isEnabled) } }
        )
        handleApiCall(
            apiCall = { getCalendarPositionUseCase() },
            onSuccess = { position -> setState { copy(calendarPosition = position) } }
        )
        handleApiCall(
            apiCall = { getCalendarFontColorUseCase() },
            onSuccess = { color -> setState { copy(calendarFontColor = color) } }
        )
    }

    fun toggleCalendarOverlay(isEnabled: Boolean) {
        handleApiCall(
            apiCall = { setCalendarOverlayEnabledUseCase(isEnabled) },
            onSuccess = { setState { copy(isCalendarOverlayEnabled = isEnabled) } }
        )
    }

    fun setCalendarPosition(position: Int) {
        handleApiCall(
            apiCall = { setCalendarPositionUseCase(position) },
            onSuccess = { setState { copy(calendarPosition = position) } }
        )
    }

    fun setCalendarFontColor(color: Int) {
        handleApiCall(
            apiCall = { setCalendarFontColorUseCase(color) },
            onSuccess = { setState { copy(calendarFontColor = color) } }
        )
    }
}
