package pion.tech.pionbase.feature.onboard.viewpager

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.LayoutIapBinding
import pion.tech.pionbase.feature.onboard.OnboardViewModel

@AndroidEntryPoint
class OnboardScreen5Fragment :
    BaseFragment<LayoutIapBinding, OnboardViewModel>(
        LayoutIapBinding::inflate,
        OnboardViewModel::class.java,
    ) {
    override fun init(view: View) {
        initView()
    }

    private fun initView() {
    }

    override fun subscribeObserver(view: View) {
        // Subscribe to any observers if needed
    }
}
