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

fun PreviewWallpaperFragment.settingEvent() {
    binding.btnBack.setPreventDoubleClick {
        navigator.navigateUp()
    }

    binding.btnSetWallpaper.setPreventDoubleClickScaleView {
        val path = arguments?.getString("wallpaperPath") ?: ""
        if (path.isNotEmpty()) {
            viewModel.setWallpaperPath(path)
        }
    }
}
