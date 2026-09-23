package pion.tech.pionbase.data.repository.wallpaper

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.util.Result

interface WallpaperRepository {
    fun saveGifToInternalStorage(uri: Uri): Flow<Result<String>>
    fun saveVideoToInternalStorage(uri: Uri): Flow<Result<String>>
    fun saveImageToInternalStorage(uri: Uri): Flow<Result<String>>
    fun saveUrlToInternalStorage(urlString: String, isVideo: Boolean): Flow<Result<String>>
    fun downloadWallpaperToGallery(
        pathOrUrl: String,
        isVideo: Boolean,
        isGif: Boolean,
        isCalendarEnabled: Boolean = false,
        calendarPosition: Int = 2,
        calendarColor: Int = -1
    ): Flow<Result<String>>
}
