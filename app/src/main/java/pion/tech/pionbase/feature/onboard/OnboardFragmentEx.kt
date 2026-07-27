package pion.tech.pionbase.feature.onboard

import pion.tech.pionbase.R
import pion.tech.pionbase.feature.onboard.adapter.OnboardFragmentStateAdapter

fun OnboardFragment.initView() {
    adapter = OnboardFragmentStateAdapter(this)
    binding.vpMain.adapter = adapter
}

fun OnboardFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun OnboardFragment.backEvent() {
}

fun OnboardFragment.previousPage() {
    binding.vpMain.currentItem -= 1
}

fun OnboardFragment.nextPage() {
    if (binding.vpMain.currentItem == adapter?.itemCount?.minus(1)) {
        goToHomeEvent()
        return
    }
    binding.vpMain.currentItem += 1
}

fun OnboardFragment.goToHomeEvent() {
    navigator.navigateTo(R.id.action_onboardFragment_to_homeFragment)
}
