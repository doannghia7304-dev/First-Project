package pion.tech.pionbase.feature.setting

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.domain.usecase.setting.GetDarkModeUseCase
import pion.tech.pionbase.domain.usecase.setting.SetDarkModeUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.*
import pion.tech.pionbase.domain.usecase.weather.GetWeatherOverlayEnabledUseCase
import pion.tech.pionbase.domain.usecase.weather.SetWeatherOverlayEnabledUseCase
import pion.tech.pionbase.util.CalendarOverlayUtils
import pion.tech.pionbase.util.handleApiCall

import pion.tech.pionbase.domain.usecase.weather.GetCurrentWeatherUseCase
import pion.tech.pionbase.data.model.weather.WeatherDtoModel

data class SettingUiState(
    val isCalendarOverlayEnabled: Boolean = false,
    val isWeatherOverlayEnabled: Boolean = false,
    val currentWeather: WeatherDtoModel = WeatherDtoModel(),
    val calendarPosition: Int = 2, // 0: Top, 1: Center, 2: Bottom
    val calendarFontColor: Int = -1, // Default white
    val darkMode: Int = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
    val currentLanguageName: String = ""
)

class SettingViewModel(
    private val getCalendarOverlayEnabledUseCase: GetCalendarOverlayEnabledUseCase,
    private val setCalendarOverlayEnabledUseCase: SetCalendarOverlayEnabledUseCase,
    private val getWeatherOverlayEnabledUseCase: GetWeatherOverlayEnabledUseCase,
    private val setWeatherOverlayEnabledUseCase: SetWeatherOverlayEnabledUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getCalendarPositionUseCase: GetCalendarPositionUseCase,
    private val setCalendarPositionUseCase: SetCalendarPositionUseCase,
    private val getCalendarFontColorUseCase: GetCalendarFontColorUseCase,
    private val setCalendarFontColorUseCase: SetCalendarFontColorUseCase,
    private val getStaticWallpaperPathUseCase: GetStaticWallpaperPathUseCase,
    private val getActiveWallpaperTypeUseCase: GetActiveWallpaperTypeUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
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
            apiCall = { getWeatherOverlayEnabledUseCase() },
            onSuccess = { isEnabled -> setState { copy(isWeatherOverlayEnabled = isEnabled) } }
        )
        handleApiCall(
            apiCall = { getCurrentWeatherUseCase() },
            onSuccess = { weatherDto -> setState { copy(currentWeather = weatherDto) } }
        )
        handleApiCall(
            apiCall = { getCalendarPositionUseCase() },
            onSuccess = { position -> setState { copy(calendarPosition = position) } }
        )
        handleApiCall(
            apiCall = { getCalendarFontColorUseCase() },
            onSuccess = { color -> setState { copy(calendarFontColor = color) } }
        )
        handleApiCall(
            apiCall = { getDarkModeUseCase() },
            onSuccess = { mode -> setState { copy(darkMode = mode) } }
        )
        loadCurrentLanguage()
    }

    private fun loadCurrentLanguage() {
        val locales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()
        val currentTag = if (!locales.isEmpty) locales[0]?.language else "en"
        val langName = when (currentTag) {
            "vi" -> "Việt Nam"
            "es" -> "Español"
            "fr" -> "Français"
            "de" -> "Deutsch"
            "ja" -> "日本人"
            "zh" -> "中國人"
            "ko" -> "한국인"
            "ru" -> "Pусский"
            else -> "English"
        }
        setState { copy(currentLanguageName = langName) }
    }

    fun setDarkMode(mode: Int) {
        handleApiCall(
            apiCall = { setDarkModeUseCase(mode) },
            onSuccess = {
                setState { copy(darkMode = mode) }
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(mode)
            }
        )
    }

    fun toggleCalendarOverlay(context: Context, isEnabled: Boolean) {
        handleApiCall(
            apiCall = { setCalendarOverlayEnabledUseCase(isEnabled) },
            onSuccess = {
                setState { copy(isCalendarOverlayEnabled = isEnabled) }
                updateStaticWallpaperIfSet(context)
            }
        )
    }

    fun toggleWeatherOverlay(context: Context, isEnabled: Boolean) {
        handleApiCall(
            apiCall = { setWeatherOverlayEnabledUseCase(isEnabled) },
            onSuccess = {
                setState { copy(isWeatherOverlayEnabled = isEnabled) }
                updateStaticWallpaperIfSet(context)
            }
        )
    }

    fun setCalendarPosition(context: Context, position: Int) {
        handleApiCall(
            apiCall = { setCalendarPositionUseCase(position) },
            onSuccess = {
                setState { copy(calendarPosition = position) }
                updateStaticWallpaperIfSet(context)
            }
        )
    }

    fun setCalendarFontColor(context: Context, color: Int) {
        handleApiCall(
            apiCall = { setCalendarFontColorUseCase(color) },
            onSuccess = {
                setState { copy(calendarFontColor = color) }
                updateStaticWallpaperIfSet(context)
            }
        )
    }

    private fun updateStaticWallpaperIfSet(context: Context) {
        handleApiCall(
            apiCall = { getActiveWallpaperTypeUseCase() },
            onSuccess = { activeType ->
                // Chỉ tự động ghi đè WallpaperManager nếu loại hình nền ĐANG HOẠT ĐỘNG là Ảnh Tĩnh!
                // Nếu đang là GIF hoặc Video Live Wallpaper, hệ thống Live Wallpaper Service đã tự quan sát và vẽ Lịch thời gian thực, không được đè hình tĩnh lên!
                if (activeType == DataStoreRepository.WALLPAPER_TYPE_STATIC) {
                    handleApiCall(
                        apiCall = { getStaticWallpaperPathUseCase() },
                        onSuccess = { staticPath ->
                            if (!staticPath.isNullOrEmpty()) {
                                val file = File(staticPath)
                                if (file.exists()) {
                                    launchIO {
                                        try {
                                            val srcBitmap = BitmapFactory.decodeFile(file.absolutePath)
                                            val isCalendarEnabled = uiState.value.isCalendarOverlayEnabled
                                            val isWeatherEnabled = uiState.value.isWeatherOverlayEnabled
                                            val weather = uiState.value.currentWeather
                                            val position = uiState.value.calendarPosition
                                            val color = uiState.value.calendarFontColor

                                            var finalBitmap = srcBitmap
                                            if (finalBitmap != null) {
                                                if (isCalendarEnabled) {
                                                    finalBitmap = CalendarOverlayUtils.drawCalendarOnBitmap(finalBitmap, true, position, color)
                                                }
                                                if (isWeatherEnabled) {
                                                    finalBitmap = pion.tech.pionbase.util.weather.WeatherCanvasOverlay.drawWeatherOnBitmap(finalBitmap, true, weather, context)
                                                }
                                            }

                                            val wallpaperManager = WallpaperManager.getInstance(context)
                                            if (finalBitmap != null) {
                                                val tempFile = File(context.filesDir, "final_static_wallpaper.jpg")
                                                tempFile.outputStream().use { out ->
                                                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                                                }
                                                tempFile.inputStream().use { stream ->
                                                    wallpaperManager.setStream(stream, null, true, WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
                                                }
                                            }
                                        } catch (e: Exception) {
                                            // Ignore
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        )
    }
}
