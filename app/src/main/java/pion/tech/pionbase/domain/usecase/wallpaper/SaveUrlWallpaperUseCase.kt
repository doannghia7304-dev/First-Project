package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.wallpaper.WallpaperRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class SaveUrlWallpaperUseCase(
    private val wallpaperRepository: WallpaperRepository
) : BaseUseCase() {

    operator fun invoke(url: String, isVideo: Boolean): Flow<Result<String>> = executeFlow(dispatcher = Dispatchers.IO) {
        wallpaperRepository.saveUrlToInternalStorage(url, isVideo)
    }
}
