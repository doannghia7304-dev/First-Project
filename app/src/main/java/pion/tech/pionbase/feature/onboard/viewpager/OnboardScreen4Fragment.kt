package pion.tech.pionbase.feature.onboard.viewpager

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardScreen4Binding
import pion.tech.pionbase.feature.onboard.OnboardFragment
import pion.tech.pionbase.feature.onboard.OnboardViewModel
import pion.tech.pionbase.feature.onboard.goToHomeEvent
import pion.tech.pionbase.util.setPreventDoubleClick

@AndroidEntryPoint
class OnboardScreen4Fragment :
    BaseFragment<FragmentOnboardScreen4Binding, OnboardViewModel>(
        FragmentOnboardScreen4Binding::inflate,
        OnboardViewModel::class.java,
    ) {
    override fun init(view: View) {
        nextEvent()
    }

    private fun nextEvent() {
        binding.btnNext.setPreventDoubleClick {
            (parentFragment as? OnboardFragment)?.goToHomeEvent()
        }
    }

    override fun subscribeObserver(view: View) {
        // Subscribe to any observers if needed
    }
}
