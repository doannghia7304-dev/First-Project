package pion.tech.pionbase.feature.previewWallpaper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentPreviewWallpaperBinding
import pion.tech.pionbase.service.GifWallpaperService
import pion.tech.pionbase.service.VideoWallpaperService
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.R

class PreviewWallpaperFragment : BaseFragment<FragmentPreviewWallpaperBinding, PreviewWallpaperViewModel>(
    FragmentPreviewWallpaperBinding::inflate,
    PreviewWallpaperViewModel::class
) {

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        applyEvent()
        onBackEvent()
        val path = arguments?.getString("wallpaperPath") ?: ""
        if (path.isNotEmpty()) {
            viewModel.checkFavorite(path)
        }
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.isFavorite }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isFav ->
                val color = if (isFav) android.graphics.Color.RED else android.graphics.Color.WHITE
                androidx.core.widget.ImageViewCompat.setImageTintList(binding.btnFavorite, android.content.res.ColorStateList.valueOf(color))
            }

        viewModel.uiEvent.collectFlowOnView(viewLifecycleOwner) { event ->
            when (event) {
                is PreviewWallpaperUiEvent.WallpaperSavedSuccessfully -> {
                    displayToast(getString(R.string.success_set_wallpaper))
                    openWallpaperPicker(event.isVideo)
                }
            }
        }
    }

    private fun openWallpaperPicker(isVideo: Boolean) {
        val component = if (isVideo) {
            ComponentName(requireContext(), VideoWallpaperService::class.java)
        } else {
            ComponentName(requireContext(), GifWallpaperService::class.java)
        }
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component)
        }
        startActivity(intent)
    }
}
