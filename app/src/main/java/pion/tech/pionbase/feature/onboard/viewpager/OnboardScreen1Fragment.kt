package pion.tech.pionbase.feature.onboard.viewpager

import android.os.Bundle
import android.view.View
import org.koin.androidx.viewmodel.ext.android.viewModel
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardScreen1Binding
import pion.tech.pionbase.feature.onboard.OnboardViewModel
import pion.tech.pionbase.util.setPreventDoubleClick

class OnboardScreen1Fragment :
    BaseFragment<FragmentOnboardScreen1Binding, OnboardViewModel>(
        FragmentOnboardScreen1Binding::inflate,
        OnboardViewModel::class,
    ) {
    private val parentViewModel: OnboardViewModel by viewModel(ownerProducer = { requireParentFragment() })

    override fun init(view: View, savedInstanceState: Bundle?) {
        nextEvent()
    }

    private fun nextEvent() {
        binding.btnNext.setPreventDoubleClick {
            parentViewModel.nextPage()
        }
    }

    override fun subscribeObserver(view: View) {
        // Subscribe to any observers if needed
    }

    companion object {
        fun newInstance() = OnboardScreen1Fragment()
    }
}
