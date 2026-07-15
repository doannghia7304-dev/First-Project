package pion.tech.pionbase.feature.onboard

import android.os.Bundle
import android.view.View
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardBinding
import pion.tech.pionbase.feature.onboard.adapter.OnboardFragmentStateAdapter

class OnboardFragment :
    BaseFragment<FragmentOnboardBinding, OnboardViewModel>(
        FragmentOnboardBinding::inflate,
        OnboardViewModel::class,
    ) {
    var adapter: OnboardFragmentStateAdapter? = null

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
    }
}
