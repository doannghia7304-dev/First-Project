package pion.tech.pionbase.feature.previewWallpaper

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.domain.usecase.wallpaper.SetLiveWallpaperPathUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SetVideoWallpaperPathUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class PreviewWallpaperViewModel(
    private val setLiveWallpaperPathUseCase: SetLiveWallpaperPathUseCase,
    private val setVideoWallpaperPathUseCase: SetVideoWallpaperPathUseCase
) : BaseViewModel<PreviewWallpaperUiState, PreviewWallpaperUiEvent>(PreviewWallpaperUiState()) {

    fun setWallpaperPath(path: String, isVideo: Boolean) {
        setState { copy(setWallpaperState = UiState.Loading) }
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
}

data class PreviewWallpaperUiState(
    val setWallpaperState: UiState<Unit> = UiState.None
)

sealed interface PreviewWallpaperUiEvent {
    data class WallpaperSavedSuccessfully(val isVideo: Boolean) : PreviewWallpaperUiEvent
}
