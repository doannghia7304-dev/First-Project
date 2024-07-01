package pion.tech.pionbase.framework.presentation.home

import pion.tech.pionbase.util.BundleKey
import pion.tech.pionbase.util.parcelable
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun HomeFragment.initView() {
    dummyEntity = arguments?.parcelable(BundleKey.KEY_DUMMY_ENTITY)
    binding.tvHome.text = dummyEntity?.value
}

fun HomeFragment.plusEvent(){
    binding.btnPlus.setPreventDoubleClickScaleView {
        viewModel.plusValue()
    }
}
