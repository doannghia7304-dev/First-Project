package pion.tech.pionbase.feature.onboard.viewpager

import android.os.Bundle
import android.view.View
import org.koin.androidx.viewmodel.ext.android.viewModel
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardScreen1Binding
import pion.tech.pionbase.feature.onboard.OnboardViewModel

class OnboardScreen1Fragment :
    BaseFragment<FragmentOnboardScreen1Binding, OnboardViewModel>(
        FragmentOnboardScreen1Binding::inflate,
        OnboardViewModel::class,
    ) {
    override fun init(view: View, savedInstanceState: Bundle?) {
    }

    override fun subscribeObserver(view: View) {
        // Subscribe to any observers if needed
    }

    companion object {
        fun newInstance() = OnboardScreen1Fragment()
    }
}
