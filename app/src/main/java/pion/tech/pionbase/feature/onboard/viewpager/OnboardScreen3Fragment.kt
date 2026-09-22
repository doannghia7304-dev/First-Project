package pion.tech.pionbase.feature.onboard.viewpager

import android.os.Bundle
import android.view.View
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardScreen3Binding
import pion.tech.pionbase.feature.onboard.OnboardViewModel

class OnboardScreen3Fragment :
    BaseFragment<FragmentOnboardScreen3Binding, OnboardViewModel>(
        FragmentOnboardScreen3Binding::inflate,
        OnboardViewModel::class,
    ) {
    override fun init(view: View, savedInstanceState: Bundle?) {
    }

    override fun subscribeObserver(view: View) {
        // Subscribe to any observers if needed
    }

    companion object {
        fun newInstance() = OnboardScreen3Fragment()
    }
}
