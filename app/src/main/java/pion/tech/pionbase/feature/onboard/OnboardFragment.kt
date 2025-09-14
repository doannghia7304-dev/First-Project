package pion.tech.pionbase.feature.onboard

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardBinding
import pion.tech.pionbase.feature.onboard.adapter.OnboardFragmentStateAdapter

@AndroidEntryPoint
class OnboardFragment :
    BaseFragment<FragmentOnboardBinding, OnboardViewModel, >(
        FragmentOnboardBinding::inflate,
        OnboardViewModel::class.java,
    ) {
    var adapter: OnboardFragmentStateAdapter? = null

    override fun init(view: View) {
        initView()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
    }
}
