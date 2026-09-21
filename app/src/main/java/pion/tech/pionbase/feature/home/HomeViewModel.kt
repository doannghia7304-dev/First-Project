package pion.tech.pionbase.feature.home

import android.net.Uri
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.data.model.installedApp.toPresentation
import pion.tech.pionbase.domain.usecase.home.GetInstalledAppsUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SaveGifWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SaveImageWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SaveVideoWallpaperUseCase
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class HomeViewModel(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val saveGifWallpaperUseCase: SaveGifWallpaperUseCase,
    private val saveVideoWallpaperUseCase: SaveVideoWallpaperUseCase,
    private val saveImageWallpaperUseCase: SaveImageWallpaperUseCase,
) : BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState()) {

    init {
        getInstalledApps()
    }

    fun saveSelectedGif(uri: Uri) {
        setState { copy(saveGifState = UiState.Loading) }
        handleApiCall(
            apiCall = { saveGifWallpaperUseCase(uri) },
            onSuccess = { path ->
                setState { copy(saveGifState = UiState.Success(path)) }
                launchMain {
                    setEvent(HomeUiEvent.NavigateToPreview(path, isVideo = false, isStatic = false))
                }
            },
            onError = { throwable ->
                setState { copy(saveGifState = UiState.Error(throwable)) }
            }
        )
    }

    fun saveSelectedVideo(uri: Uri) {
        setState { copy(saveVideoState = UiState.Loading) }
        handleApiCall(
            apiCall = { saveVideoWallpaperUseCase(uri) },
            onSuccess = { path ->
                setState { copy(saveVideoState = UiState.Success(path)) }
                launchMain {
                    setEvent(HomeUiEvent.NavigateToPreview(path, isVideo = true, isStatic = false))
                }
            },
            onError = { throwable ->
                setState { copy(saveVideoState = UiState.Error(throwable)) }
            }
        )
    }

    fun saveSelectedImage(uri: Uri) {
        setState { copy(saveGifState = UiState.Loading) }
        handleApiCall(
            apiCall = { saveImageWallpaperUseCase(uri) },
            onSuccess = { path ->
                setState { copy(saveGifState = UiState.Success(path)) }
                launchMain {
                    setEvent(HomeUiEvent.NavigateToPreview(path, isVideo = false, isStatic = true))
                }
            },
            onError = { throwable ->
                setState { copy(saveGifState = UiState.Error(throwable)) }
            }
        )
    }

    fun getInstalledApps() {
        setState { copy(installedAppsUiState = UiState.Loading) }

        handleApiCall(
            apiCall = { getInstalledAppsUseCase() },
            onSuccess = { dtoList ->
                val installedApps = dtoList.map { it.toPresentation() }
                setState { copy(installedAppsUiState = UiState.Success(installedApps)) }
            },
            onError = { throwable ->
                setState { copy(installedAppsUiState = UiState.Error(throwable)) }
            },
        )
    }
}

data class HomeUiState(
    val installedAppsUiState: UiState<List<InstalledAppUIModel>> = UiState.None,
    val saveGifState: UiState<String> = UiState.None,
    val saveVideoState: UiState<String> = UiState.None,
)

sealed interface HomeUiEvent {
    data class NavigateToPreview(val path: String, val isVideo: Boolean, val isStatic: Boolean = false) : HomeUiEvent
}
