package pion.tech.pionbase.framework.presentation.home

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.framework.database.entities.DummyEntity
import pion.tech.pionbase.framework.presentation.common.BaseFragment
import pion.tech.pionbase.framework.presentation.home.adapter.DemoMultipleAdapter
import pion.tech.pionbase.framework.presentation.home.dialog.DemoDialog
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate,
    HomeViewModel::class.java
), DemoDialog.Listener {

    var dummyEntity: DummyEntity? = null
    val adapter = DemoMultipleAdapter()
    override fun init(view: View) {
        initView()
        plusEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.countValue.collectFlowOnView(viewLifecycleOwner) {
//            binding.tvCount.text = "$it"
            binding.tvCount.text = prefUtil.isPremium.toString()
        }
    }

    override fun onDialogPositiveClick() {
    }

    override fun onDialogNegativeClick() {
        displayToast("Hello")
    }

}
