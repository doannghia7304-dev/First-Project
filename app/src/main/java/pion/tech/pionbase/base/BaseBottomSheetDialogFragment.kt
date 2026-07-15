package pion.tech.pionbase.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseAnalyticsLogger
import timber.log.Timber

abstract class BaseBottomSheetDialogFragment<T : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T
) : BottomSheetDialogFragment() {
    val logger: FirebaseAnalyticsLogger by inject()

    private var _binding: T? = null

    protected val binding: T
        get() =
            checkNotNull(_binding) {
                "BottomSheetDialogFragment ${this::class.simpleName} binding cannot be accessed before onCreateView() or after onDestroyView()"
            }

    protected inline fun binding(block: T.() -> Unit): T = binding.apply(block)

    override fun onAttach(context: Context) {
        Timber.d("${this::class.simpleName} onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("${this::class.simpleName} onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Timber.d("${this::class.simpleName} onCreateView")
        _binding = inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        Timber.d("${this::class.simpleName} onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        initData(savedInstanceState)
        initView(savedInstanceState)
        addEvent(savedInstanceState)
    }

    open fun initView(savedInstanceState: Bundle?) {}

    open fun addEvent(savedInstanceState: Bundle?) {}

    open fun initData(savedInstanceState: Bundle?) {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                findViewById<View>(R.id.design_bottom_sheet)
                    ?.setBackgroundResource(android.R.color.transparent)
            }
        }

    // Lifecycle methods with logging
    override fun onStart() {
        Timber.d("${this::class.simpleName} onStart")
        super.onStart()
    }

    override fun onResume() {
        Timber.d("${this::class.simpleName} onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.d("${this::class.simpleName} onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.d("${this::class.simpleName} onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.d("${this::class.simpleName} onDestroyView")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Timber.d("${this::class.simpleName} onDestroy")
        super.onDestroy()
    }

    fun show(manager: FragmentManager) {
        show(manager, tag)
    }

    override fun show(
        manager: FragmentManager,
        tag: String?,
    ) {
        if (isVisible) {
            return
        }
        runCatching {
            manager.beginTransaction().remove(this).commit()
            super.show(manager, tag)
        }
    }

    override fun dismiss() {
        runCatching {
            super.dismiss()
        }
    }
}
