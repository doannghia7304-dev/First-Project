package pion.tech.pionbase.feature.previewWallpaper

import android.app.WallpaperManager
import java.io.File
import pion.tech.pionbase.R
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClick
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun PreviewWallpaperFragment.initView() {
    val path = arguments?.getString("wallpaperPath") ?: ""
    if (path.isNotEmpty()) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            binding.ivPreview.loadImage(path)
        } else {
            binding.ivPreview.loadImage(File(path))
        }
    }
}

fun PreviewWallpaperFragment.applyEvent() {
    val path = arguments?.getString("wallpaperPath") ?: ""
    val isVideo = arguments?.getBoolean("isVideo") ?: false
    val isStatic = arguments?.getBoolean("isStatic") ?: false

    binding.btnFavorite.setPreventDoubleClickScaleView {
        if (path.isNotEmpty()) {
            viewModel.toggleFavorite(path, isVideo, isStatic)
        }
    }

    binding.btnSetWallpaper.setPreventDoubleClickScaleView {
        if (path.isNotEmpty()) {
            if (isStatic) {
                showHideLoading(true)
                launchIO {
                    try {
                        val file = if (path.startsWith("http://") || path.startsWith("https://")) {
                            val url = java.net.URL(path)
                            val connection = url.openConnection() as java.net.HttpURLConnection
                            connection.doInput = true
                            connection.connect()
                            val inputStream = connection.inputStream
                            val localFile = File(requireContext().filesDir, "static_wallpaper_${System.currentTimeMillis()}.jpg")
                            localFile.outputStream().use { output ->
                                inputStream.copyTo(output)
                            }
                            localFile
                        } else {
                            File(path)
                        }

                        if (file.exists()) {
                            val wallpaperManager = WallpaperManager.getInstance(requireContext())
                            file.inputStream().use { stream ->
                                wallpaperManager.setStream(stream, null, true, WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
                            }
                            launchMain {
                                showHideLoading(false)
                                displayToast(getString(R.string.success_set_wallpaper))
                                navigator.navigateUp()
                            }
                        } else {
                            launchMain {
                                showHideLoading(false)
                                displayToast(getString(R.string.something_error))
                            }
                        }
                    } catch (e: Exception) {
                        launchMain {
                            showHideLoading(false)
                            displayToast(e.message ?: getString(R.string.something_error))
                        }
                    }
                }
            } else {
                if (path.startsWith("http://") || path.startsWith("https://")) {
                    viewModel.setWallpaperUrl(path, isVideo)
                } else {
                    viewModel.setWallpaperPath(path, isVideo)
                }
            }
        }
    }
}

fun PreviewWallpaperFragment.onBackEvent() {
    onSystemBack {
        navigator.navigateUp()
    }
    
    binding.btnBack.setPreventDoubleClick {
        navigator.navigateUp()
    }
}
