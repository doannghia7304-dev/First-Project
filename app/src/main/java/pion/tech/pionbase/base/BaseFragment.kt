package pion.tech.pionbase.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import pion.tech.pionbase.app.ApiViewModel
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseAnalyticsLogger
import pion.tech.pionbase.base.navigator.Navigator
import pion.tech.pionbase.base.navigator.NavigatorImpl
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import timber.log.Timber
import kotlin.reflect.KClass

typealias Inflate<Binding> = (LayoutInflater, ViewGroup?, Boolean) -> Binding

abstract class BaseFragment<Binding : ViewBinding, VM : ViewModel>(
    private val inflate: Inflate<Binding>,
    private val viewModelClass: KClass<VM>,
) : Fragment() {
    val logger: FirebaseAnalyticsLogger by inject()
    val dataStoreRepository: DataStoreRepository by inject()

    private var _navigator: Navigator? = null
    val navigator: Navigator
        get() =
            checkNotNull(_navigator) {
                "Fragment $this navigator cannot be accessed before onCreateView() or after onDestroyView()"
            }

    private var _binding: Binding? = null

    val binding: Binding
        get() =
            checkNotNull(_binding) {
                "Fragment $this binding cannot be accessed before onCreateView() or after onDestroyView()"
            }

    protected val bindingOrNull: Binding?
        get() = _binding

    val commonViewModel: CommonViewModel by activityViewModel()
    val apiViewModel: ApiViewModel by activityViewModel()

    open val viewModel: VM by viewModelForClass(viewModelClass)

    private var isInit = false
    private var saveView = false

    private var destChangeListener = NavController.OnDestinationChangedListener { _, _, _ ->
        showHideLoading(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (saveView) {
            if (_binding == null) {
                isInit = true
                _binding = inflate.invoke(inflater, container, false)
            } else {
                isInit = false
            }
        } else {
            _binding = inflate.invoke(inflater, container, false)
        }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val currentDestinationId = findNavController().currentDestination?.id ?: 0
        _navigator = NavigatorImpl(findNavController(), lifecycle, currentDestinationId)
        _navigator?.addOnDestinationChangedListener(listener = destChangeListener)
        init(view, savedInstanceState)
        subscribeObserver(view)
    }

    abstract fun init(view: View, savedInstanceState: Bundle?)

    abstract fun subscribeObserver(view: View)

    override fun onDestroyView() {
        hideLoading()
        _navigator?.removeOnDestinationChangedListener(destChangeListener)
        _binding = null
        super.onDestroyView()
    }

    private var loadingDialog: LoadingDialog? = null

    /**
     * Show or hide loading dialog
     * @param isShow true to show, false to hide
     */
    fun showHideLoading(isShow: Boolean) {
        if (isShow) {
            showLoading()
        } else {
            hideLoading()
        }
    }

    private fun showLoading() {
        if (loadingDialog == null || loadingDialog?.isVisible == false) {
            loadingDialog?.dismiss()
            loadingDialog = LoadingDialog()
            loadingDialog?.show(childFragmentManager)
        }
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    fun onSystemBack(action: () -> Unit) {
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            action.invoke()
        }
    }

    companion object {
        private const val TAG = "BaseFragment"
    }
}

fun Fragment.doActionWhenResume(action: () -> Unit) {
    if (isResumed) {
        action()
        return
    }

    lifecycle.addObserver(
        object : LifecycleEventObserver {
            override fun onStateChanged(
                source: LifecycleOwner,
                event: Lifecycle.Event,
            ) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    action.invoke()
                    lifecycle.removeObserver(this)
                }
            }
        },
    )
}

fun Fragment.doActionWhenStop(action: () -> Unit) {
    lifecycle.addObserver(
        object : LifecycleEventObserver {
            override fun onStateChanged(
                source: LifecycleOwner,
                event: Lifecycle.Event,
            ) {
                if (event == Lifecycle.Event.ON_STOP) {
                    action.invoke()
                    lifecycle.removeObserver(this)
                }
            }
        },
    )
}

/**
 * Launch a coroutine with exception handling
 * @param dispatcher The dispatcher to use (default: Dispatchers.IO)
 * @param onError Callback for error handling
 * @param block The coroutine block to execute
 */
private fun Fragment.launchWithExceptionHandler(
    dispatcher: kotlinx.coroutines.CoroutineDispatcher,
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Timber.e("${this::class.java.simpleName} error: $throwable")
            lifecycleScope.launch(Dispatchers.Main) {
                onError(throwable)
            }
        }
    return lifecycleScope.launch(dispatcher + exceptionHandler, block = block)
}

fun Fragment.launchIO(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
): Job = launchWithExceptionHandler(Dispatchers.IO, onError, block)

fun Fragment.launchDefault(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
): Job = launchWithExceptionHandler(Dispatchers.Default, onError, block)

fun Fragment.launchMain(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
): Job = launchWithExceptionHandler(Dispatchers.Main, onError, block)
