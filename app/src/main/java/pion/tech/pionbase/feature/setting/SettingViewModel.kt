package pion.tech.pionbase.feature.setting

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.domain.usecase.wallpaper.*
import pion.tech.pionbase.util.CalendarOverlayUtils
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
    private val setCalendarFontColorUseCase: SetCalendarFontColorUseCase,
    private val getStaticWallpaperPathUseCase: GetStaticWallpaperPathUseCase,
    private val getActiveWallpaperTypeUseCase: GetActiveWallpaperTypeUseCase,
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

    fun toggleCalendarOverlay(context: Context, isEnabled: Boolean) {
        handleApiCall(
            apiCall = { setCalendarOverlayEnabledUseCase(isEnabled) },
            onSuccess = {
                setState { copy(isCalendarOverlayEnabled = isEnabled) }
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
                                            val position = uiState.value.calendarPosition
                                            val color = uiState.value.calendarFontColor

                                            val finalBitmap = if (srcBitmap != null && isCalendarEnabled) {
                                                CalendarOverlayUtils.drawCalendarOnBitmap(srcBitmap, true, position, color)
                                            } else {
                                                srcBitmap
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
