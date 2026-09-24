package pion.tech.pionbase.feature.home

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import pion.tech.pionbase.R
import pion.tech.pionbase.feature.home.dialog.CategoryBottomSheetDialog
import pion.tech.pionbase.feature.home.dialog.ExitAppDialog
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun HomeFragment.initView() {
    // Setup Categories Horizontal List
    binding.rvCategories.apply {
        categoryAdapter.setListener(this@initView)
        adapter = categoryAdapter
    }

    // Setup Main Wallpaper Staggered Grid (Figma style)
    binding.rvMain.apply {
        templateAdapter.setListener(this@initView)
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = templateAdapter
    }

    setupGreeting()
}

fun HomeFragment.setupGreeting() {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val greetingRes = when (hour) {
        in 5..11 -> R.string.good_morning
        in 12..17 -> R.string.good_afternoon
        else -> R.string.good_evening
    }
    binding.tvGreeting.setText(greetingRes)
}

fun HomeFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun HomeFragment.backEvent() {
    val dialog = ExitAppDialog()
    dialog.show(childFragmentManager)
}

fun HomeFragment.settingEvent() {
    binding.btnHome.setPreventDoubleClickScaleView {
        binding.rvMain.smoothScrollToPosition(0)
    }
    binding.btnCategories.setPreventDoubleClickScaleView {
        val categoryUiState = apiViewModel.uiState.value.categoryUiState
        if (categoryUiState is UiState.Success && categoryUiState.data.isNotEmpty()) {
            val dialog = CategoryBottomSheetDialog(categoryUiState.data) { selectedCat ->
                apiViewModel.selectCategory(selectedCat.id)
            }
            dialog.show(childFragmentManager)
        } else {
            binding.rvCategories.smoothScrollToPosition(0)
            binding.rvMain.smoothScrollToPosition(0)
        }
    }
    binding.btnSetting.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_settingFragment)
    }
    binding.btnFavorites.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_favoritesFragment)
    }
    binding.ivSearch.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_searchFragment)
    }
}
