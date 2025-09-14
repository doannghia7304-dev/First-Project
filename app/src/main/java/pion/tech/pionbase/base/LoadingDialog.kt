package pion.tech.pionbase.base

import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.DialogLoadingBinding

@AndroidEntryPoint
class LoadingDialog : BaseDialogFragment<DialogLoadingBinding>(R.layout.dialog_loading)
