package pion.tech.pionbase.feature.home.dialog

import android.os.Bundle
import pion.tech.pionbase.base.BaseDialogFragment
import pion.tech.pionbase.databinding.DialogExitAppBinding
import pion.tech.pionbase.util.setPreventDoubleClick
import kotlin.system.exitProcess

class ExitAppDialog : BaseDialogFragment<DialogExitAppBinding>(DialogExitAppBinding::inflate) {
    override fun addEvent(savedInstanceState: Bundle?) {
        super.addEvent(savedInstanceState)
        binding.btnCancel.setPreventDoubleClick {
            dismiss()
        }
        binding.btnExit.setPreventDoubleClick {
            exitProcess(0)
        }
    }
}
