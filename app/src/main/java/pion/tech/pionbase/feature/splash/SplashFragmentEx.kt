package pion.tech.pionbase.feature.splash

import android.animation.ValueAnimator
import pion.tech.pionbase.R
import pion.tech.pionbase.util.safeDelay

fun SplashFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun SplashFragment.backEvent() {
    // Nothing
}

fun SplashFragment.releaseAnimation() {
    progressAnimator?.cancel()
    progressAnimator = null
}

fun SplashFragment.initView() {
    binding.progressBar.apply {
        isIndeterminate = false
        progress = 0
    }

    // Đồng bộ thời gian chạy ProgressBar khớp với 3 giây của màn hình Splash
    progressAnimator =
        ValueAnimator.ofInt(0, 100).apply {
            duration = 3000L

            addUpdateListener { animation ->
                runCatching {
                    binding.progressBar.progress = animation.animatedValue as Int
                }
            }
            start()
        }
}

fun SplashFragment.goToNextScreen() {
    // Lấy giá trị thực tế từ ViewModel sau khi đã delay xong
    val actualSelected = viewModel.uiState.value.isLanguageSelected ?: false
    
    val destination =
        if (actualSelected || isCameFromLanguage()) {
            R.id.action_splashFragment_to_homeFragment
        } else {
            R.id.action_splashFragment_to_languageFragment
        }
    navigator.navigateTo(destination)
}

fun SplashFragment.isCameFromLanguage(): Boolean = navigator.isCameFrom(R.id.languageFragment)

fun SplashFragment.showAds(isLanguageSelected: Boolean) {
    safeDelay(3000L) {
        goToNextScreen()
    }
}

fun SplashFragment.preloadLanguageAds() {
    if (isCameFromLanguage()) return
}
