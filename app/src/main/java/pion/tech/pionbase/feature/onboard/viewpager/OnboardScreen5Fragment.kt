package pion.tech.pionbase.feature.onboard.viewpager

import android.os.Bundle
import android.view.View
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.LayoutIapBinding
import pion.tech.pionbase.feature.onboard.OnboardViewModel

class OnboardScreen5Fragment :
    BaseFragment<LayoutIapBinding, OnboardViewModel>(
        LayoutIapBinding::inflate,
        OnboardViewModel::class,
    ) {
    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
    }

    override fun subscribeObserver(view: View) {
        // Subscribe to any observers if needed
    }
}
