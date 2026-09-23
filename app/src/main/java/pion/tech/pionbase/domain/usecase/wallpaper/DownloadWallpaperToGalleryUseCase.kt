package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.wallpaper.WallpaperRepository
import pion.tech.pionbase.util.Result

class DownloadWallpaperToGalleryUseCase(
    private val wallpaperRepository: WallpaperRepository
) {
    operator fun invoke(
        pathOrUrl: String,
        isVideo: Boolean,
        isGif: Boolean = false,
        isCalendarEnabled: Boolean = false,
        calendarPosition: Int = 2,
        calendarColor: Int = -1
    ): Flow<Result<String>> = wallpaperRepository.downloadWallpaperToGallery(
        pathOrUrl,
        isVideo,
        isGif,
        isCalendarEnabled,
        calendarPosition,
        calendarColor
    )
}
