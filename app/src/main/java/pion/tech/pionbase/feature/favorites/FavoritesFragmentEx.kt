package pion.tech.pionbase.feature.favorites

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import pion.tech.pionbase.util.setPreventDoubleClick

fun FavoritesFragment.initView() {
    binding.rvFavorites.apply {
        templateAdapter.setListener(this@initView)
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = templateAdapter
    }
}

fun FavoritesFragment.onBackEvent() {
    onSystemBack {
        navigator.navigateUp()
    }
    binding.btnBack.setPreventDoubleClick {
        navigator.navigateUp()
    }
}
