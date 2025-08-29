package pion.tech.pionbase.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.piontech.core.base.BaseFragment
import com.piontech.core.base.launchIO
import com.piontech.core.lifecycleCallback.FragmentLifecycleAction
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pion.datlt.libads.AdsController
import pion.datlt.libads.utils.AdsConstant
import pion.tech.pionbase.R
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import javax.inject.Inject

class FragmentLifecycleActionImpl
    @Inject
    constructor(
        private val dataStoreRepository: DataStoreRepository,
    ) : FragmentLifecycleAction {
        private var jobSetBlockAds: Job? = null

        override fun executeWhenCreated(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>) {
        }

        override fun executeWhenStarted(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>) {
        }

        override fun executeWhenResume(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>) {
            val config: Boolean = AdsConstant.listConfigAds["appresume"]?.isOn ?: false
            fragment.launchIO {
                if (Constant.isPremiumValue(dataStoreRepository) || fragment.navigator.getCurrentDestinationId() == R.id.splashFragment ||
                    fragment.navigator.getCurrentDestinationId() == R.id.onboardFragment ||
                    !config
                ) {
                    AdsController.Companion.isBlockOpenAds = true
                } else {
                    jobSetBlockAds =
                        fragment.lifecycleScope.launch {
                            delay(1000L)
                            if (fragment.lifecycle.currentState == Lifecycle.State.RESUMED) {
                                AdsController.Companion.isBlockOpenAds = false
                            }
                        }
                }
            }
        }

        override fun executeWhenPaused(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>) {
        }

        override fun executeWhenStop(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>) {
            jobSetBlockAds?.cancel()
        }

        override fun executeWhenViewDestroyed(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>) {
        }

        override fun executeWhenDestroyed(fragment: BaseFragment<out ViewBinding, out ViewModel, out ViewModel>) {
        }
    }
