package pion.tech.pionbase.framework.presentation.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import pion.tech.pionbase.util.PrefUtil
import timber.log.Timber
import javax.inject.Inject

abstract class BaseDialogFragment<T : ViewDataBinding>(
    @LayoutRes private val contentLayoutId: Int,
) : DialogFragment() {

    @Inject
    lateinit var prefUtil: PrefUtil

    private var bindingComponent: DataBindingComponent? = DataBindingUtil.getDefaultComponent()

    private var _binding: T? = null

    protected val binding: T
        get() = checkNotNull(_binding) {
            "DialogFragment ${this::class.simpleName} binding cannot be accessed before onCreateView() or after onDestroyView()"
        }

    protected inline fun binding(block: T.() -> Unit): T {
        return binding.apply(block)
    }

    override fun onAttach(context: Context) {
        Timber.d("${this::class.simpleName} onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("${this::class.simpleName} onCreate $savedInstanceState")
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
        Timber.d("${this::class.simpleName} onViewCreated $savedInstanceState")
        super.onViewCreated(view, savedInstanceState)

        val dialog = this.dialog
        if (dialog != null) {
            val window = dialog.window
            if (window != null) {
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                val layoutParams = window.attributes
                layoutParams.gravity = Gravity.CENTER
                window.attributes = layoutParams

                window.decorView.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val inputMethodManager =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                    false
                }
            }
        }
        initData(savedInstanceState)
        initView(savedInstanceState)
        addEvent(savedInstanceState)
    }

    open fun initView(savedInstanceState: Bundle?) {}

    open fun addEvent(savedInstanceState: Bundle?) {}

    open fun initData(savedInstanceState: Bundle?) {}

    fun setDialogCanCancel() {
        val dialog = this.dialog
        if (dialog != null) {
            val window = dialog.window
            if (window != null) {
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                dialog.setCancelable(true)
                dialog.setCanceledOnTouchOutside(true)
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