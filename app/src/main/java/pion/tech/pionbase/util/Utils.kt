package pion.tech.pionbase.util

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.piontech.core.base.BaseBottomSheetDialogFragment
import com.piontech.core.base.BaseDialogFragment
import com.piontech.core.base.doActionWhenResume
import pion.datlt.libads.utils.DialogNative

fun Fragment.safeShowDialog(
    dialog: BaseDialogFragment<out ViewDataBinding>?,
    fragmentManager: FragmentManager = childFragmentManager,
) {
    if (dialog == null) return
    DialogNative.runWhenNativeDismiss {
        if (isResumed) {
            dialog.show(fragmentManager, tag)
        } else {
            doActionWhenResume {
                dialog.show(fragmentManager, tag)
            }
        }
    }
}

fun Fragment.safeShowBottomSheet(
    dialog: BaseBottomSheetDialogFragment<out ViewDataBinding>?,
    fragmentManager: FragmentManager = childFragmentManager,
) {
    if (dialog == null) return
    DialogNative.runWhenNativeDismiss {
        if (isResumed) {
            dialog.show(fragmentManager, tag)
        } else {
            doActionWhenResume {
                dialog.show(fragmentManager, tag)
            }
        }
    }
}
