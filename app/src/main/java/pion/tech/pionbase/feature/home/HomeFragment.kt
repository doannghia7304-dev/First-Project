package pion.tech.pionbase.feature.home

import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.base.BaseFragment
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
                        categoryAdapter.submitList(categories)

                        if (categories.isNotEmpty()) {
                            apiViewModel.getTemplate(categories[0].id)
                        }
                    },
                    onError = { throwable ->
                        displayToast("Error categories: ${throwable.message}")
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
        // Khi người dùng click vào một category khác trên giao diện, 
        // Gọi API lấy danh sách hình nền mới tương ứng với ID của category đó.
        apiViewModel.getTemplate(item.id)
    }

    override fun onClickTemplate(item: TemplateUIModel, position: Int) {
        displayToast("Opening wallpaper details...")
    }

    // Photo Picker Integration (Security & Privacy focused)
    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.ivSelectedMedia.isVisible = true
            binding.ivSelectedMedia.loadImage(uri)
        }
    }
    
    private fun initPickMedia() {
        binding.btnPickMedia.setPreventDoubleClick {
            pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
    }
}
