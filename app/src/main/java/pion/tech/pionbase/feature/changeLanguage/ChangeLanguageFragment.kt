package pion.tech.pionbase.feature.changeLanguage

import android.animation.ValueAnimator
import android.view.View
import kotlinx.coroutines.Job
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentChangeLanguageBinding

class ChangeLanguageFragment :
    BaseFragment<FragmentChangeLanguageBinding, ChangeLanguageViewModel>(
        FragmentChangeLanguageBinding::inflate,
        ChangeLanguageViewModel::class,
    ) {
    var progressAnimator: ValueAnimator? = null
    var preloadJob: Job? = null

    override fun init(view: View) {
        initView()
        preloadAndNav()
    }

    override fun subscribeObserver(view: View) {
    }

    override fun onDestroyView() {
        preloadJob?.cancel()
        releaseAnimation()
        super.onDestroyView()
    }
}
