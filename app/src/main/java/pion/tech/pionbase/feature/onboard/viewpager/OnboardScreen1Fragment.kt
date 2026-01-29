package pion.tech.pionbase.feature.onboard.viewpager

import android.view.View
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardScreen1Binding
import pion.tech.pionbase.feature.onboard.OnboardFragment
import pion.tech.pionbase.feature.onboard.OnboardViewModel
import pion.tech.pionbase.feature.onboard.nextPage
import pion.tech.pionbase.util.setPreventDoubleClick

class OnboardScreen1Fragment :
    BaseFragment<FragmentOnboardScreen1Binding, OnboardViewModel>(
        FragmentOnboardScreen1Binding::inflate,
        OnboardViewModel::class,
    ) {
    override fun init(view: View) {
        nextEvent()
    }

    private fun nextEvent() {
        binding.btnNext.setPreventDoubleClick {
            (parentFragment as? OnboardFragment)?.nextPage()
        }
    }

    override fun subscribeObserver(view: View) {
        // Subscribe to any observers if needed
    }
}
