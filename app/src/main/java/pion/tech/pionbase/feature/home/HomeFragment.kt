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
        // Lắng nghe sự kiện chuyển đổi màn hình từ HomeViewModel
        viewModel.uiEvent.collectFlowOnView(viewLifecycleOwner) { event ->
            when (event) {
                is HomeUiEvent.NavigateToPreview -> {
                    val bundle = Bundle().apply {
                        putString("wallpaperPath", event.path)
                        putBoolean("isVideo", event.isVideo)
                        putBoolean("isStatic", event.isStatic)
                    }
                    navigator.navigateTo(R.id.action_homeFragment_to_previewWallpaperFragment, bundle)
                }
            }
        }

        // Lắng nghe trạng thái lưu file GIF để hiển thị Loading
        viewModel.uiState
            .map { it.saveGifState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { saveState ->
                saveState.handleUiState(
                    onLoading = { showHideLoading(true) },
                    onSuccess = { showHideLoading(false) },
                    onError = { throwable ->
                        showHideLoading(false)
                        displayToast(getString(R.string.error_load_gif))
                    }
                )
            }

        // Lắng nghe trạng thái lưu file Video để hiển thị Loading
        viewModel.uiState
            .map { it.saveVideoState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { saveState ->
                saveState.handleUiState(
                    onLoading = { showHideLoading(true) },
                    onSuccess = { showHideLoading(false) },
                    onError = { throwable ->
                        showHideLoading(false)
                        displayToast(getString(R.string.error_load_video))
                    }
                )
            }

        // Observe thời tiết từ API Retrofit để hiển thị Weather Badge trên màn hình Home
        viewModel.uiState
            .map { it.weatherUiState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { state ->
                state.handleUiState(
                    onSuccess = { weather ->
                        val symbol = when (weather.condition) {
                            pion.tech.pionbase.data.model.weather.WeatherCondition.SUNNY -> "☀️"
                            pion.tech.pionbase.data.model.weather.WeatherCondition.RAINY -> "🌧️"
                            pion.tech.pionbase.data.model.weather.WeatherCondition.CLOUDY -> "☁️"
                            pion.tech.pionbase.data.model.weather.WeatherCondition.SNOWY -> "❄️"
                            pion.tech.pionbase.data.model.weather.WeatherCondition.THUNDERSTORM -> "🌩️"
                            null -> "☀️"
                        }
                        if (weather.temperatureC != null) {
                            binding.tvWeatherBadge.text = "$symbol ${weather.temperatureC}°C"
                            binding.tvWeatherBadge.isVisible = true
                        } else {
                            binding.tvWeatherBadge.text = symbol
                            binding.tvWeatherBadge.isVisible = true
                        }
                    }
                )
            }

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
                        displayToast(getString(R.string.something_error))
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
        val path = item.imageModel ?: item.thumbnail ?: ""
        if (path.isNotEmpty()) {
            val isVideo = item.templateType?.contains("video", ignoreCase = true) == true || item.videoPreview != null || path.endsWith(".mp4", true)
            val isGif = item.templateType?.contains("gif", ignoreCase = true) == true || path.endsWith(".gif", true)
            val isStatic = !isVideo && !isGif
            val bundle = Bundle().apply {
                putString("wallpaperPath", path)
                putBoolean("isVideo", isVideo)
                putBoolean("isStatic", isStatic)
            }
            navigator.navigateTo(R.id.action_homeFragment_to_previewWallpaperFragment, bundle)
        } else {
            displayToast(getString(R.string.something_error))
        }
    }

    // Photo Picker Integration (Security & Privacy focused for GIF and Video MP4)
    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val mimeType = requireContext().contentResolver.getType(uri) ?: ""
            if (mimeType.contains("video", ignoreCase = true)) {
                viewModel.saveSelectedVideo(uri)
            } else if (mimeType.contains("gif", ignoreCase = true) || uri.toString().endsWith(".gif", true)) {
                viewModel.saveSelectedGif(uri)
            } else {
                viewModel.saveSelectedImage(uri)
            }
        }
    }
    
    private fun initPickMedia() {
        binding.btnPickMedia.setPreventDoubleClick {
            pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
    }
}
