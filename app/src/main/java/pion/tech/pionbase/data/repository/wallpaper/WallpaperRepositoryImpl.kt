package pion.tech.pionbase.data.repository.wallpaper

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result
import java.io.File

class WallpaperRepositoryImpl(
    private val context: Context
) : BaseRepository(), WallpaperRepository {

    override fun saveGifToInternalStorage(uri: Uri): Flow<Result<String>> = executeDataCall {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw java.io.FileNotFoundException("Could not open input stream for Uri: $uri")
        
        val localFile = File(context.filesDir, "selected_wallpaper.gif")
        localFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        localFile.absolutePath
    }
}
