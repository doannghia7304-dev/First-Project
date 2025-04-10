package pion.tech.pionbase.framework.presentation.common.lifecycleCallback

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import timber.log.Timber

class FragmentLifecycleCallbacksImpl : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        Timber.d("${f::class.simpleName} onFragmentCreated")
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.simpleName} onFragmentStarted")
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.simpleName} onFragmentResumed")
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.simpleName} onFragmentPaused")
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.simpleName} onFragmentStopped")
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
        Timber.d("${f::class.simpleName} onFragmentSaveInstanceState")
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.simpleName} onFragmentViewDestroyed")
    }
    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.simpleName} onFragmentDestroyed")
    }
}