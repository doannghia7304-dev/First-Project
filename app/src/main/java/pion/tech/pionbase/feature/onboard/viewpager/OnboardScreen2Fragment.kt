package pion.tech.pionbase.feature.onboard.viewpager

import android.os.Bundle
import android.view.View
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardScreen2Binding
import pion.tech.pionbase.feature.onboard.OnboardViewModel

class OnboardScreen2Fragment :
    BaseFragment<FragmentOnboardScreen2Binding, OnboardViewModel>(
        FragmentOnboardScreen2Binding::inflate,
        OnboardViewModel::class,
    ) {
    override fun init(view: View, savedInstanceState: Bundle?) {
    }

    override fun subscribeObserver(view: View) {
        // Subscribe to any observers if needed
    }

    companion object {
        fun newInstance() = OnboardScreen2Fragment()
    }
}
