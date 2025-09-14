package pion.tech.pionbase.feature.home

import pion.tech.pionbase.R
import pion.tech.pionbase.feature.home.bottomSheet.DemoBottomSheet
import pion.tech.pionbase.feature.home.dialog.DemoDialog
import pion.tech.pionbase.util.safeShowDialog
import pion.tech.pionbase.util.setPreventDoubleClick
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun HomeFragment.initView() {
    binding.rvMain.adapter = adapter
}

fun HomeFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun HomeFragment.backEvent() {
}

fun HomeFragment.settingEvent() {
    binding.btnSetting.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_settingFragment)
    }
}

fun HomeFragment.showDemoDialogEvent() {
    binding.btnShowDialog.setPreventDoubleClick {
        val dialog = DemoDialog.newInstance("Demo Dialog")
        dialog.setListener(this)
        safeShowDialog(dialog)
    }
}
