package pion.tech.pionbase.feature.onboard

import android.os.Bundle
import android.view.View
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentOnboardBinding
import pion.tech.pionbase.feature.onboard.adapter.OnboardFragmentStateAdapter
import pion.tech.pionbase.util.collectFlowOnView

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
        viewModel.uiEvent.collectFlowOnView(viewLifecycleOwner) { event ->
            when (event) {
                is OnboardEvent.NextPage -> nextPage()
                is OnboardEvent.PreviousPage -> previousPage()
                is OnboardEvent.IAPSkipBtnClicked -> {
                    //TODO: xử lý event click skip của Onboard Iap
                }
                is OnboardEvent.GoToHomeScreen -> goToHomeEvent()
            }
        }
    }
}
