package pion.tech.pionbase.feature.onboard

import android.view.View
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentOnboardBinding
import pion.tech.pionbase.feature.onboard.adapter.OnboardFragmentStateAdapter

@AndroidEntryPoint
class OnboardFragment :
    BaseFragment<FragmentOnboardBinding, OnboardViewModel, CommonViewModel>(
        FragmentOnboardBinding::inflate,
        OnboardViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    var adapter: OnboardFragmentStateAdapter? = null

    override fun init(view: View) {
        initView()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
    }
}
