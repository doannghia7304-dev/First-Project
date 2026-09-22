package pion.tech.pionbase.feature.previewWallpaper

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.domain.usecase.favorite.AddFavoriteUseCase
import pion.tech.pionbase.domain.usecase.favorite.CheckIsFavoriteUseCase
import pion.tech.pionbase.domain.usecase.favorite.RemoveFavoriteUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SaveUrlWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SetLiveWallpaperPathUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SetVideoWallpaperPathUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class PreviewWallpaperViewModel(
    private val setLiveWallpaperPathUseCase: SetLiveWallpaperPathUseCase,
    private val setVideoWallpaperPathUseCase: SetVideoWallpaperPathUseCase,
    private val saveUrlWallpaperUseCase: SaveUrlWallpaperUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase,
) : BaseViewModel<PreviewWallpaperUiState, PreviewWallpaperUiEvent>(PreviewWallpaperUiState()) {

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
}

data class PreviewWallpaperUiState(
    val setWallpaperState: UiState<Unit> = UiState.None,
    val isFavorite: Boolean = false,
)

sealed interface PreviewWallpaperUiEvent {
    data class WallpaperSavedSuccessfully(val isVideo: Boolean) : PreviewWallpaperUiEvent
}
