package pion.tech.pionbase.feature.onboard.viewpager

import android.os.Bundle
import android.view.View
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardScreen2Binding
import pion.tech.pionbase.feature.onboard.OnboardFragment
import pion.tech.pionbase.feature.onboard.OnboardViewModel
import pion.tech.pionbase.feature.onboard.nextPage
import pion.tech.pionbase.util.setPreventDoubleClick

class OnboardScreen2Fragment :
    BaseFragment<FragmentOnboardScreen2Binding, OnboardViewModel>(
        FragmentOnboardScreen2Binding::inflate,
        OnboardViewModel::class,
    ) {
    override fun init(view: View, savedInstanceState: Bundle?) {
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

    companion object {
        fun newInstance() = OnboardScreen2Fragment()
    }
}
