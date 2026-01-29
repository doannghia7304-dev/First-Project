package pion.tech.pionbase.feature.setting.dialog

import android.os.Bundle
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseDialogFragment
import pion.tech.pionbase.databinding.DialogAdvertisementBinding
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

class AdvertisementDialog : BaseDialogFragment<DialogAdvertisementBinding>(R.layout.dialog_advertisement) {
    override fun addEvent(savedInstanceState: Bundle?) {
        super.addEvent(savedInstanceState)
        binding.ivClose.setPreventDoubleClickScaleView {
            dismiss()
        }
    }
}
