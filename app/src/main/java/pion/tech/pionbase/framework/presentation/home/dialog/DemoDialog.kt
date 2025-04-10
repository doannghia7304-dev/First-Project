package pion.tech.pionbase.framework.presentation.home.dialog

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.framework.presentation.common.BaseDialogFragment
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.DialogDemoBinding
import pion.tech.pionbase.util.BundleKey

@AndroidEntryPoint
class DemoDialog : BaseDialogFragment<DialogDemoBinding>(
    R.layout.dialog_demo
) {

    private var listener: Listener? = null

    interface Listener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    private var param1: String = ""

    override fun initView(savedInstanceState: Bundle?) {
        binding.tvTitle.text = param1
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        param1 = arguments?.getString(BundleKey.KEY_DUMMY_ENTITY).toString()
    }

    override fun addEvent(savedInstanceState: Bundle?) {
        binding.btnClose.setOnClickListener {
            listener?.onDialogNegativeClick()
        }
    }

    companion object {
        fun newInstance(dummyTitle: String): DemoDialog {
            return DemoDialog().apply {
                arguments = Bundle().apply {
                    putString(BundleKey.KEY_DUMMY_ENTITY, dummyTitle)
                }
            }
        }
    }
}