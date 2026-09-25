package pion.tech.pionbase.feature.previewWallpaper

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.domain.usecase.favorite.AddFavoriteUseCase
import pion.tech.pionbase.domain.usecase.favorite.CheckIsFavoriteUseCase
import pion.tech.pionbase.domain.usecase.favorite.RemoveFavoriteUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.DownloadWallpaperToGalleryUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetCalendarFontColorUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetCalendarOverlayEnabledUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetCalendarPositionUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SaveUrlWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SetActiveWallpaperTypeUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SetLiveWallpaperPathUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SetStaticWallpaperPathUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SetVideoWallpaperPathUseCase
import pion.tech.pionbase.domain.usecase.weather.GetCurrentWeatherUseCase
import pion.tech.pionbase.domain.usecase.weather.GetWeatherOverlayEnabledUseCase
import pion.tech.pionbase.data.model.weather.WeatherDtoModel
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class PreviewWallpaperViewModel(
    private val setLiveWallpaperPathUseCase: SetLiveWallpaperPathUseCase,
    private val setVideoWallpaperPathUseCase: SetVideoWallpaperPathUseCase,
    private val setStaticWallpaperPathUseCase: SetStaticWallpaperPathUseCase,
    private val setActiveWallpaperTypeUseCase: SetActiveWallpaperTypeUseCase,
    private val saveUrlWallpaperUseCase: SaveUrlWallpaperUseCase,
    private val downloadWallpaperToGalleryUseCase: DownloadWallpaperToGalleryUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase,
    private val getCalendarOverlayEnabledUseCase: GetCalendarOverlayEnabledUseCase,
    private val getCalendarPositionUseCase: GetCalendarPositionUseCase,
    private val getCalendarFontColorUseCase: GetCalendarFontColorUseCase,
    private val getWeatherOverlayEnabledUseCase: GetWeatherOverlayEnabledUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
) : BaseViewModel<PreviewWallpaperUiState, PreviewWallpaperUiEvent>(PreviewWallpaperUiState()) {

    init {
        loadCalendarSettings()
        loadWeatherSettings()
    }

    private fun loadWeatherSettings() {
        handleApiCall(
            apiCall = { getWeatherOverlayEnabledUseCase() },
            onSuccess = { isEnabled -> setState { copy(isWeatherEnabled = isEnabled) } }
        )
        handleApiCall(
            apiCall = { getCurrentWeatherUseCase() },
            onSuccess = { weatherDto -> setState { copy(currentWeather = weatherDto) } }
        )
    }

    private fun loadCalendarSettings() {
        handleApiCall(
            apiCall = { getCalendarOverlayEnabledUseCase() },
            onSuccess = { isEnabled -> setState { copy(isCalendarEnabled = isEnabled) } }
        )
        handleApiCall(
            apiCall = { getCalendarPositionUseCase() },
            onSuccess = { pos -> setState { copy(calendarPosition = pos) } }
        )
        handleApiCall(
            apiCall = { getCalendarFontColorUseCase() },
            onSuccess = { color -> setState { copy(calendarColor = color) } }
        )
    }

    fun saveStaticWallpaperPath(path: String) {
        handleApiCall(
            apiCall = { setStaticWallpaperPathUseCase(path) },
            onSuccess = {
                handleApiCall(apiCall = { setActiveWallpaperTypeUseCase(DataStoreRepository.WALLPAPER_TYPE_STATIC) })
            }
        )
    }

    fun checkFavorite(wallpaperId: String) {
        handleApiCall(
            apiCall = { checkIsFavoriteUseCase(wallpaperId) },
            onSuccess = { isFav ->
                setState { copy(isFavorite = isFav) }
            }
        )
    }

    fun toggleFavorite(path: String, isVideo: Boolean, isStatic: Boolean) {
        val dto = TemplateDtoModel(
            thumbnail = path,
            imageModel = path,
            videoPreview = if (isVideo) path else null,
            templateType = when {
                isVideo -> "video"
                path.endsWith(".gif", true) -> "gif"
                else -> "image"
            }
        )
        val currentFav = uiState.value.isFavorite
        handleApiCall(
            apiCall = {
                if (currentFav) {
                    removeFavoriteUseCase(dto)
                } else {
                    addFavoriteUseCase(dto)
                }
            },
            onSuccess = {
                setState { copy(isFavorite = !currentFav) }
            }
        )
    }

    fun setWallpaperPath(path: String, isVideo: Boolean) {
        setState { copy(setWallpaperState = UiState.Loading) }
        val type = if (isVideo) DataStoreRepository.WALLPAPER_TYPE_VIDEO else DataStoreRepository.WALLPAPER_TYPE_GIF
        handleApiCall(
            apiCall = { setActiveWallpaperTypeUseCase(type) }
        )
        handleApiCall(
            apiCall = { 
                if (isVideo) {
                    setVideoWallpaperPathUseCase(path)
                } else {
                    setLiveWallpaperPathUseCase(path)
                }
            },
            onSuccess = {
                setState { copy(setWallpaperState = UiState.Success(Unit)) }
                launchMain {
                    setEvent(PreviewWallpaperUiEvent.WallpaperSavedSuccessfully(isVideo))
                }
            },
            onError = { throwable ->
                setState { copy(setWallpaperState = UiState.Error(throwable)) }
            }
        )
    }

    fun setWallpaperUrl(url: String, isVideo: Boolean) {
        setState { copy(setWallpaperState = UiState.Loading) }
        handleApiCall(
            apiCall = { saveUrlWallpaperUseCase(url, isVideo) },
            onSuccess = { localPath ->
                setWallpaperPath(localPath, isVideo)
            },
            onError = { throwable ->
                setState { copy(setWallpaperState = UiState.Error(throwable)) }
            }
        )
    }

    fun downloadToGallery(pathOrUrl: String, isVideo: Boolean, isGif: Boolean = false) {
        setState { copy(downloadState = UiState.Loading) }
        val actualGif = isGif || pathOrUrl.endsWith(".gif", ignoreCase = true)
        val currentState = uiState.value
        handleApiCall(
            apiCall = {
                downloadWallpaperToGalleryUseCase(
                    pathOrUrl = pathOrUrl,
                    isVideo = isVideo,
                    isGif = actualGif,
                    isCalendarEnabled = currentState.isCalendarEnabled,
                    calendarPosition = currentState.calendarPosition,
                    calendarColor = currentState.calendarColor
                )
            },
            onSuccess = { savedPath ->
                setState { copy(downloadState = UiState.Success(savedPath)) }
                launchMain {
                    setEvent(PreviewWallpaperUiEvent.WallpaperDownloadedSuccessfully(savedPath))
                }
            },
            onError = { throwable ->
                timber.log.Timber.e(throwable, "Download to gallery failed")
                setState { copy(downloadState = UiState.Error(throwable)) }
            }
        )
    }
}

data class PreviewWallpaperUiState(
    val setWallpaperState: UiState<Unit> = UiState.None,
    val downloadState: UiState<String> = UiState.None,
    val isFavorite: Boolean = false,
    val isCalendarEnabled: Boolean = false,
    val isWeatherEnabled: Boolean = false,
    val currentWeather: WeatherDtoModel = WeatherDtoModel(),
    val calendarPosition: Int = 2,
    val calendarColor: Int = -1
)

sealed interface PreviewWallpaperUiEvent {
    data class WallpaperSavedSuccessfully(val isVideo: Boolean) : PreviewWallpaperUiEvent
    data class WallpaperDownloadedSuccessfully(val savedPath: String) : PreviewWallpaperUiEvent
}
