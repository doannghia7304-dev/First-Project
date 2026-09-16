package pion.tech.pionbase.feature.home

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import pion.tech.pionbase.R
import pion.tech.pionbase.feature.home.dialog.ExitAppDialog
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
    binding.btnSetting.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_settingFragment)
    }
}
