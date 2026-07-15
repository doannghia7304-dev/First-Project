package pion.tech.pionbase.feature.home.dialog

import android.os.Bundle
import pion.tech.pionbase.base.BaseDialogFragment
import pion.tech.pionbase.databinding.DialogDemoBinding
import pion.tech.pionbase.util.BundleKey

class DemoDialog :
    BaseDialogFragment<DialogDemoBinding>(
        DialogDemoBinding::inflate,
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
            dismiss()
        }
    }

    companion object {
        fun newInstance(dummyTitle: String): DemoDialog =
            DemoDialog().apply {
                arguments =
                    Bundle().apply {
                        putString(BundleKey.KEY_DUMMY_ENTITY, dummyTitle)
                    }
            }
    }
}
