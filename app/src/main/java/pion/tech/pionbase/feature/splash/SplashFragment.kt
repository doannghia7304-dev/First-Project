package pion.tech.pionbase.feature.splash

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import pion.tech.pionbase.app.MainActivity
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSplashBinding
import pion.tech.pionbase.util.collectFlowOnView

class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel>(
        FragmentSplashBinding::inflate,
        SplashViewModel::class,
    ) {
    var progressAnimator: ValueAnimator? = null

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        showAds()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseAnimation()
    }
}
