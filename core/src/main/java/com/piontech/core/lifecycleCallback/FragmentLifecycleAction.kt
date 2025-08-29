package com.piontech.core.lifecycleCallback

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.piontech.core.base.BaseFragment

interface FragmentLifecycleAction {
    fun executeWhenCreated(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>)

    fun executeWhenStarted(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>)

    fun executeWhenResume(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>)

    fun executeWhenPaused(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>)

    fun executeWhenStop(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>)

    fun executeWhenViewDestroyed(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>)

    fun executeWhenDestroyed(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>)
}
