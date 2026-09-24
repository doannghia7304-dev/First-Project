package pion.tech.pionbase.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import timber.log.Timber
import java.io.File

object MediaStoreUtils {

    fun saveMediaToGallery(
        context: Context,
        pathOrUrl: String,
        isVideo: Boolean,
        isGif: Boolean = false,
        isCalendarEnabled: Boolean = false,
        calendarPosition: Int = 2,
        calendarColor: Int = -1
    ): String {
        val tempFile = downloadToTempFile(context, pathOrUrl, isVideo, isGif)
            ?: throw java.io.IOException("Failed to download or access source file")

        var calendarTempFile: File? = null

        try {
            val fileToSave = if (!isVideo && !isGif && isCalendarEnabled) {
                calendarTempFile = applyCalendarOverlayToTempFile(
                    context,
                    tempFile,
                    calendarPosition,
                    calendarColor
                )
                calendarTempFile ?: tempFile
            } else {
                tempFile
            }

            val extension = when {
                isVideo -> "mp4"
                isGif || pathOrUrl.endsWith(".gif", ignoreCase = true) -> "gif"
                else -> "jpg"
            }
            val fileName = "WallpaperMaster_${System.currentTimeMillis()}.$extension"
            val mimeType = when {
                isVideo -> "video/mp4"
                extension == "gif" -> "image/gif"
                else -> "image/jpeg"
            }

            var savedUriString: String? = null
            var savedFilePath: String? = null

            val nowSeconds = System.currentTimeMillis() / 1000
            val nowMillis = System.currentTimeMillis()
            val folderName = if (isVideo) "Movies/WallpaperMaster" else "Pictures/WallpaperMaster"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val collection = if (isVideo) {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, folderName)
                        put(MediaStore.MediaColumns.DATE_ADDED, nowSeconds)
                        put(MediaStore.MediaColumns.DATE_MODIFIED, nowSeconds)
                        put(MediaStore.MediaColumns.DATE_TAKEN, nowMillis)
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }

                    val uri: Uri? = context.contentResolver.insert(collection, contentValues)
                    if (uri != null) {
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            fileToSave.inputStream().use { input ->
                                input.copyTo(outputStream)
                            }
                        }

                        contentValues.clear()
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                        context.contentResolver.update(uri, contentValues, null, null)

                        savedUriString = uri.toString()
                    }
                } catch (e: Exception) {
                    Timber.e(e, "MediaStore insert failed, falling back to File API")
                }
            }

            if (savedUriString == null) {
                val subFolder = if (isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES
                val publicDir = Environment.getExternalStoragePublicDirectory(subFolder)
                val targetDir = File(publicDir, "WallpaperMaster")

                val finalDir = if (targetDir.exists() || targetDir.mkdirs()) {
                    targetDir
                } else {
                    val appDir = context.getExternalFilesDir(subFolder) ?: context.filesDir
                    val subAppDir = File(appDir, "WallpaperMaster")
                    subAppDir.mkdirs()
                    subAppDir
                }

                val targetFile = File(finalDir, fileName)
                targetFile.parentFile?.mkdirs()

                fileToSave.inputStream().use { input ->
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                savedFilePath = targetFile.absolutePath
            }

            val resolvedFilePath = if (savedUriString != null) {
                getFilePathFromUri(context, Uri.parse(savedUriString))
            } else {
                savedFilePath
            }

            val scanPath = resolvedFilePath ?: "/storage/emulated/0/$folderName/$fileName"

            MediaScannerConnection.scanFile(
                context,
                arrayOf(scanPath),
                arrayOf(mimeType)
            ) { path, uri ->
                Timber.d("MediaScannerConnection scanned $path -> $uri")
            }

            try {
                @Suppress("DEPRECATION")
                val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                    data = if (savedUriString != null) Uri.parse(savedUriString) else Uri.fromFile(File(scanPath))
                }
                context.sendBroadcast(scanIntent)
            } catch (e: Exception) {
                Timber.w(e, "Broadcast SCAN_FILE failed")
            }

            return savedUriString ?: savedFilePath ?: ""

        } finally {
            try {
                tempFile.delete()
                calendarTempFile?.delete()
            } catch (e: Exception) {
                Timber.w(e, "Failed to delete temp files")
            }
        }
    }

    private fun downloadToTempFile(
        context: Context,
        pathOrUrl: String,
        isVideo: Boolean,
        isGif: Boolean
    ): File? {
        val extension = when {
            isVideo -> "mp4"
            isGif || pathOrUrl.endsWith(".gif", ignoreCase = true) -> "gif"
            else -> "jpg"
        }
        val tempFile = File(context.cacheDir, "temp_download_${System.currentTimeMillis()}.$extension")

        return try {
            if (pathOrUrl.startsWith("http://", ignoreCase = true) || pathOrUrl.startsWith("https://", ignoreCase = true)) {
                val url = java.net.URL(pathOrUrl)
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                )
                connection.instanceFollowRedirects = true
                connection.connectTimeout = 20000
                connection.readTimeout = 20000
                connection.doInput = true
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    throw java.io.IOException("HTTP download error: $responseCode ${connection.responseMessage}")
                }

                connection.inputStream.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } else {
                val sourceFile = File(pathOrUrl)
                if (!sourceFile.exists()) {
                    throw java.io.FileNotFoundException("Source file not found at $pathOrUrl")
                }
                sourceFile.copyTo(tempFile, overwrite = true)
            }
            tempFile
        } catch (e: Exception) {
            Timber.e(e, "Failed to prepare temp file for $pathOrUrl")
            tempFile.delete()
            null
        }
    }

    private fun applyCalendarOverlayToTempFile(
        context: Context,
        sourceFile: File,
        calendarPosition: Int,
        calendarColor: Int
    ): File? {
        return try {
            val srcBitmap = BitmapFactory.decodeFile(sourceFile.absolutePath) ?: return null
            val finalBitmap = CalendarOverlayUtils.drawCalendarOnBitmap(
                srcBitmap,
                isCalendarEnabled = true,
                calendarPosition = calendarPosition,
                calendarFontColor = calendarColor
            )
            val calendarTempFile = File(context.cacheDir, "temp_calendar_${System.currentTimeMillis()}.jpg")
            calendarTempFile.outputStream().use { out ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            calendarTempFile
        } catch (e: Exception) {
            Timber.e(e, "Failed to apply calendar overlay")
            null
        }
    }

    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        return try {
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                if (columnIndex != -1 && cursor.moveToFirst()) {
                    cursor.getString(columnIndex)
                } else null
            }
        } catch (e: Exception) {
            Timber.w(e, "Could not resolve file path from URI: $uri")
            null
        }
    }
}
