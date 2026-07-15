package pion.tech.pionbase.feature.setting.dialog

import android.os.Bundle
import pion.tech.pionbase.base.BaseDialogFragment
import pion.tech.pionbase.databinding.DialogDeveloperBinding
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

class DeveloperDialog : BaseDialogFragment<DialogDeveloperBinding>(DialogDeveloperBinding::inflate) {
    override fun addEvent(savedInstanceState: Bundle?) {
        super.addEvent(savedInstanceState)
        binding.ivClose.setPreventDoubleClickScaleView {
            dismiss()
        }
    }
}
