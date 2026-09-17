package pion.tech.pionbase.feature.home

import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.data.model.appCategory.AppCategoryUIModel
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.adapter.CategoryAdapter
import pion.tech.pionbase.feature.home.adapter.TemplateAdapter
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClick

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class,
    ),
    CategoryAdapter.Listener,
    TemplateAdapter.Listener {
    
    val categoryAdapter = CategoryAdapter()
    val templateAdapter = TemplateAdapter()

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        settingEvent()
        initPickMedia()
        onBackEvent()
    }
    override fun subscribeObserver(view: View) {
        // Observe danh sách Categories từ API thật thông qua activityViewModel (apiViewModel)
        apiViewModel.uiState
            .map { it.categoryUiState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { uiState ->
                uiState.handleUiState(
                    onSuccess = { categories ->
                        // submitList sẽ tự động Diff dữ liệu nhờ vào isSelected trong Model
                        categoryAdapter.submitList(categories)

                        // Tự động load wallpapers cho category đầu tiên nếu chưa có cái nào được chọn
                        if (categories.isNotEmpty() && categories.none { it.isSelected }) {
                            apiViewModel.selectCategory(categories[0].id)
                        }
                    },
                    onError = { throwable ->
                        displayToast(msg = getString(R.string.error_categories,throwable.message?:""))
                    }
                )
            }

        // Observe danh sách Wallpapers từ API thật
        apiViewModel.uiState
            .map { it.templateUiState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { uiState ->
                uiState.handleUiState(
                    onSuccess = { templates ->
                        templateAdapter.submitList(templates)
                    },
                    onError = { throwable ->
                        displayToast("Error wallpapers: ${throwable.message}")
                    }
                )
            }
    }

    override fun onClickCategory(item: AppCategoryUIModel, position: Int) {
        // Gọi thẳng vào ViewModel để cập nhật trạng thái chọn
        // Luồng dữ liệu sẽ chảy ngược lại: ViewModel update State -> Fragment Observe -> Adapter submitList
        apiViewModel.selectCategory(item.id)
    }

    override fun onClickTemplate(item: TemplateUIModel, position: Int) {
        displayToast("Opening wallpaper details...")
    }

    // Photo Picker Integration (Security & Privacy focused for GIF)
    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            launchIO {
                try {
                    val context = requireContext()
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val localFile = java.io.File(context.filesDir, "selected_wallpaper.gif")
                        localFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                        launchMain {
                            val bundle = Bundle().apply {
                                putString("wallpaperPath", localFile.absolutePath)
                            }
                            navigator.navigateTo(R.id.action_homeFragment_to_previewWallpaperFragment, bundle)
                        }
                    }
                } catch (e: Exception) {
                    launchMain {
                        displayToast(getString(R.string.error_load_gif))
                    }
                }
            }
        }
    }
    
    private fun initPickMedia() {
        binding.btnPickMedia.setPreventDoubleClick {
            pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("image/gif")))
        }
    }
}
