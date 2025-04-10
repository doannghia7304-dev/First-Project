package pion.tech.pionbase.framework.presentation.common

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pion.tech.pionbase.util.PrefUtil
import timber.log.Timber
import javax.inject.Inject

abstract class BaseBottomSheetDialogFragment<T : ViewDataBinding>(
    @LayoutRes private val contentLayoutId: Int
) : BottomSheetDialogFragment() {

    @Inject
    lateinit var prefUtil: PrefUtil

    private var bindingComponent: DataBindingComponent? = DataBindingUtil.getDefaultComponent()

    private var _binding: T? = null

    protected val binding: T
        get() = checkNotNull(_binding) {
            "BottomSheetDialogFragment ${this::class.simpleName} binding cannot be accessed before onCreateView() or after onDestroyView()"
        }

    protected inline fun binding(block: T.() -> Unit): T {
        return binding.apply(block)
    }

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
        savedInstanceState: Bundle?
    ): View {
        Timber.d("${this::class.simpleName} onCreateView")
        _binding =
            DataBindingUtil.inflate(inflater, contentLayoutId, container, false, bindingComponent)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("${this::class.simpleName} onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        initData(savedInstanceState)
        initView(savedInstanceState)
        addEvent(savedInstanceState)
    }

    open fun initView(savedInstanceState: Bundle?) {}

    open fun addEvent(savedInstanceState: Bundle?) {}

    open fun initData(savedInstanceState: Bundle?) {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
                bottomSheet?.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

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
        _binding?.unbind()
        _binding = null
    }

    override fun onDestroy() {
        Timber.d("${this::class.simpleName} onDestroy")
        super.onDestroy()
    }

    fun show(manager: FragmentManager) {
        show(manager, tag)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isVisible) {
            return
        }
        try {
            manager.beginTransaction().remove(this).commit()
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}