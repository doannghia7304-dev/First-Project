package pion.tech.pionbase.util

import android.os.Handler
import android.os.Looper
import androidx.viewbinding.ViewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pion.tech.pionbase.base.BaseBottomSheetDialogFragment
import pion.tech.pionbase.base.BaseDialogFragment
import pion.tech.pionbase.base.doActionWhenResume
import java.lang.Exception

fun Fragment.safeShowDialog(
    dialog: BaseDialogFragment<out ViewBinding>?,
    fragmentManager: FragmentManager = childFragmentManager,
) {
    if (dialog == null) return
    doActionWhenResume {
        dialog.show(fragmentManager)
    }
}

fun Fragment.safeShowBottomSheet(
    dialog: BaseBottomSheetDialogFragment<out ViewBinding>?,
    fragmentManager: FragmentManager = childFragmentManager,
) {
    if (dialog == null) return
    doActionWhenResume {
        dialog.show(fragmentManager)
    }
}

fun safeDelay(
    delayMillis: Long = 0,
    action: () -> Unit,
) {
    Handler(Looper.getMainLooper()).postDelayed({
        try {
            action()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, delayMillis)
}

inline fun <T> Flow<T>.collectFlowOnView(
    owner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline onCollect: suspend (T) -> Unit,
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(state) {
        collect {
            onCollect(it)
        }
    }
}
