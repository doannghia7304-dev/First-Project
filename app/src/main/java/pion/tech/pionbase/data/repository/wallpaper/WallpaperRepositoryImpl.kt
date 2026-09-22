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
        
        // Thêm timestamp vào tên file để bẻ gãy cơ chế tự động cache của ImageDecoder hệ thống
        val fileName = "selected_wallpaper_${System.currentTimeMillis()}.gif"
        val localFile = File(context.filesDir, fileName)
        
        // Xóa các file gif cũ trong thư mục trước khi ghi file mới để tránh rác bộ nhớ nội bộ
        context.filesDir.listFiles { _, name -> name.startsWith("selected_wallpaper_") && name.endsWith(".gif") }
            ?.forEach { it.delete() }

        localFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        localFile.absolutePath
    }

    override fun saveVideoToInternalStorage(uri: Uri): Flow<Result<String>> = executeDataCall {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw java.io.FileNotFoundException("Could not open input stream for Uri: $uri")
        
        // Thêm timestamp vào tên file để tránh trùng lặp cache
        val fileName = "selected_wallpaper_${System.currentTimeMillis()}.mp4"
        val localFile = File(context.filesDir, fileName)
        
        // Xóa các file mp4 cũ
        context.filesDir.listFiles { _, name -> name.startsWith("selected_wallpaper_") && name.endsWith(".mp4") }
            ?.forEach { it.delete() }

        localFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        localFile.absolutePath
    }

    override fun saveImageToInternalStorage(uri: Uri): Flow<Result<String>> = executeDataCall {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw java.io.FileNotFoundException("Could not open input stream for Uri: $uri")

        val fileName = "selected_wallpaper_${System.currentTimeMillis()}.jpg"
        val localFile = File(context.filesDir, fileName)

        context.filesDir.listFiles { _, name -> name.startsWith("selected_wallpaper_") && (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")) }
            ?.forEach { it.delete() }

        localFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        localFile.absolutePath
    }

    override fun saveUrlToInternalStorage(urlString: String, isVideo: Boolean): Flow<Result<String>> = executeDataCall {
        val url = java.net.URL(urlString)
        val connection = url.openConnection() as java.net.HttpURLConnection
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.doInput = true
        connection.connect()
        val inputStream = connection.inputStream

        val extension = if (isVideo) "mp4" else "gif"
        val fileName = "api_wallpaper_${System.currentTimeMillis()}.$extension"
        val localFile = File(context.filesDir, fileName)

        context.filesDir.listFiles { _, name -> name.startsWith("api_wallpaper_") }
            ?.forEach { it.delete() }

        localFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        localFile.absolutePath
    }
}
