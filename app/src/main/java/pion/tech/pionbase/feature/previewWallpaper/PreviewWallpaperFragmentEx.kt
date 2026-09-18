package pion.tech.pionbase.feature.previewWallpaper

import java.io.File
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClick
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun PreviewWallpaperFragment.initView() {
    val path = arguments?.getString("wallpaperPath") ?: ""
    if (path.isNotEmpty()) {
        binding.ivPreview.loadImage(File(path))
    }
}

fun PreviewWallpaperFragment.applyEvent() {
    binding.btnSetWallpaper.setPreventDoubleClickScaleView {
        val path = arguments?.getString("wallpaperPath") ?: ""
        val isVideo = arguments?.getBoolean("isVideo") ?: false
        if (path.isNotEmpty()) {
            viewModel.setWallpaperPath(path, isVideo)
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
