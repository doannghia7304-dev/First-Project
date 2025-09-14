package pion.tech.pionbase.feature.splash

import android.animation.ValueAnimator
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel>(
        FragmentSplashBinding::inflate,
        SplashViewModel::class.java,
    ) {
    var progressAnimator: ValueAnimator? = null

    override fun init(view: View) {
        initView()
        if (isCameFromLanguage()) {
            showAds()
            return
        }
        onBackEvent()
        initGdpr()
    }

    override fun subscribeObserver(view: View) {
        observerIapRemoteData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseAnimation()
    }
}
