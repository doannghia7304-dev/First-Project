package pion.tech.pionbase.framework.presentation.common

import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.DialogLoadingBinding

class LoadingDialog : BaseDialogFragment<DialogLoadingBinding>(R.layout.dialog_loading) {
    companion object {
        @Volatile
        private var instance: LoadingDialog? = null

        fun getInstance(): LoadingDialog {
            return instance ?: synchronized(this) {
                instance ?: LoadingDialog().also { instance = it }
            }
        }
    }
}

