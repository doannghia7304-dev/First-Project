package pion.tech.pionbase.framework.presentation.common

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding


typealias Inflate<Binding> = (LayoutInflater, ViewGroup?, Boolean) -> Binding

abstract class BaseFragment<Binding : ViewBinding , VM : ViewModel>(
    private val inflate: Inflate<Binding>,
    private val viewModelClass : Class<VM>
) : Fragment() {

    lateinit var navController: NavController

    lateinit var binding: Binding
        private set

    val commonViewModel : CommonViewModel by activityViewModels()

    val viewModel: VM by lazy {
        ViewModelProvider(this)[viewModelClass]
    }



    private var isInit = false
    var saveView = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (saveView) {
            if (binding == null) {
                isInit = true
                binding = inflate.invoke(inflater, container, false)
            } else {
                isInit = false
            }
        } else {
            binding = inflate.invoke(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        init(view)
        subscribeObserver(view)
    }

    abstract fun init(view: View)

    abstract fun subscribeObserver(view: View)

    fun safeNav(currentDestination: Int, action: Int, bundle: Bundle? = null) {
        if (navController.currentDestination?.id == currentDestination) {
            lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_RESUME) {
                        lifecycle.removeObserver(this)
                        try {
                            navController.navigate(action, bundle)
                        } catch (e: IllegalArgumentException) {
                            Log.e(TAG, "safeNav: ${e.message}")
                        }
                    }
                }
            })
        }
    }

    fun safeNav(currentDestination: Int, navDirections: NavDirections) {
        if (navController.currentDestination?.id == currentDestination) {
            lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_RESUME) {
                        lifecycle.removeObserver(this)
                        try {
                            navController.navigate(navDirections)
                        } catch (e: IllegalArgumentException) {
                            Log.e(TAG, "safeNav: ${e.message}")
                        }
                    }
                }
            })
        }
    }

    companion object {
        private const val TAG = "BaseFragment"
    }
}
