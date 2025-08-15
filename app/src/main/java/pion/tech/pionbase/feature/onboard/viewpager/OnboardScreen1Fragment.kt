package pion.tech.pionbase.feature.onboard.viewpager

import android.view.View
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentOnboardScreen1Binding
import pion.tech.pionbase.feature.onboard.OnboardFragment
import pion.tech.pionbase.feature.onboard.OnboardViewModel
import pion.tech.pionbase.feature.onboard.nextPage
import pion.tech.pionbase.util.setPreventDoubleClick

@AndroidEntryPoint
class OnboardScreen1Fragment :
    BaseFragment<FragmentOnboardScreen1Binding, OnboardViewModel, CommonViewModel>(
        FragmentOnboardScreen1Binding::inflate,
        OnboardViewModel::class.java,
        CommonViewModel::class.java,
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
