package pion.tech.pionbase.feature.onboard.viewpager

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardScreen3Binding
import pion.tech.pionbase.feature.onboard.OnboardFragment
import pion.tech.pionbase.feature.onboard.OnboardViewModel
import pion.tech.pionbase.feature.onboard.nextPage
import pion.tech.pionbase.util.setPreventDoubleClick

@AndroidEntryPoint
class OnboardScreen3Fragment :
    BaseFragment<FragmentOnboardScreen3Binding, OnboardViewModel>(
        FragmentOnboardScreen3Binding::inflate,
        OnboardViewModel::class.java,
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
