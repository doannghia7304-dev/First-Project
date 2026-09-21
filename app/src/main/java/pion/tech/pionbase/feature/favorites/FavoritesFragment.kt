package pion.tech.pionbase.feature.favorites

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.databinding.FragmentFavoritesBinding
import pion.tech.pionbase.feature.home.adapter.TemplateAdapter
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.handleUiState

class FavoritesFragment : BaseFragment<FragmentFavoritesBinding, FavoritesViewModel>(
    FragmentFavoritesBinding::inflate,
    FavoritesViewModel::class
), TemplateAdapter.Listener {

    val templateAdapter = TemplateAdapter()

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.favoritesState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { uiState ->
                uiState.handleUiState(
                    onSuccess = { list ->
                        templateAdapter.submitList(list)
                        binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    },
                    onError = {
                        binding.tvEmpty.visibility = View.VISIBLE
                    }
                )
            }
    }

    override fun onClickTemplate(item: TemplateUIModel, position: Int) {
        val path = item.imageModel ?: item.thumbnail ?: ""
        if (path.isNotEmpty()) {
            val isVideo = item.templateType?.contains("video", ignoreCase = true) == true || item.videoPreview != null
            val isStatic = !isVideo && (item.templateType?.contains("image", ignoreCase = true) == true || !path.endsWith(".gif", true))
            val bundle = Bundle().apply {
                putString("wallpaperPath", path)
                putBoolean("isVideo", isVideo)
                putBoolean("isStatic", isStatic)
            }
            navigator.navigateTo(R.id.action_favoritesFragment_to_previewWallpaperFragment, bundle)
        }
    }
}
