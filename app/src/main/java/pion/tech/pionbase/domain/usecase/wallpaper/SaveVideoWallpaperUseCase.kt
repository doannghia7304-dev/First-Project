package pion.tech.pionbase.domain.usecase.wallpaper

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.wallpaper.WallpaperRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class SaveVideoWallpaperUseCase(
    private val wallpaperRepository: WallpaperRepository
) : BaseUseCase() {

    operator fun invoke(uri: Uri): Flow<Result<String>> = executeFlow(dispatcher = Dispatchers.IO) {
        wallpaperRepository.saveVideoToInternalStorage(uri)
    }
}
