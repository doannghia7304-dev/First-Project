package pion.tech.pionbase.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import org.koin.android.ext.android.inject
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseAnalyticsLogger
import timber.log.Timber

abstract class BaseDialogFragment<T : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T
) : DialogFragment() {
    val logger: FirebaseAnalyticsLogger by inject()

    private var _binding: T? = null

    protected val binding: T
        get() =
            checkNotNull(_binding) {
                "DialogFragment ${this::class.simpleName} binding cannot be accessed before onCreateView() or after onDestroyView()"
            }

    protected inline fun binding(block: T.() -> Unit): T = binding.apply(block)

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
        Timber.d("${this::class.simpleName} onViewCreated $savedInstanceState")
        super.onViewCreated(view, savedInstanceState)
        setupDialogWindow()
        initData(savedInstanceState)
        initView(savedInstanceState)
        addEvent(savedInstanceState)
    }

    /**
     * Setup dialog window properties
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupDialogWindow() {
        dialog?.apply {
            window?.apply {
                setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                )

                decorView.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val context = context ?: return@setOnTouchListener false
                        val inputMethodManager =
                            context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)

                        if (isCancelable && isTouchOutsideContent(event)) {
                            dismiss()
                        }
                    }
                    false
                }
            }
            setCancelable(true)
        }
    }

    protected open fun getContentView(): View? = (binding.root as? ViewGroup)?.getChildAt(0)

    private fun isTouchOutsideContent(event: MotionEvent): Boolean =
        runCatching {
            val contentView = getContentView() ?: return@runCatching false
            val rect = Rect()
            contentView.getGlobalVisibleRect(rect)
            !rect.contains(event.rawX.toInt(), event.rawY.toInt())
        }.getOrDefault(false)

    open fun initView(savedInstanceState: Bundle?) {}

    open fun addEvent(savedInstanceState: Bundle?) {}

    open fun initData(savedInstanceState: Bundle?) {}

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
        // Remove touch listener to prevent memory leak
        dialog?.window?.decorView?.setOnTouchListener(null)
        _binding = null
        super.onDestroyView()
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
