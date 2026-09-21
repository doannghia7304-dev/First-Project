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
    val uiState = viewModel.uiState.value
    val isLanguageSelected = uiState.isLanguageSelected ?: false
    val isOnboardingCompleted = uiState.isOnboardingCompleted ?: false

    val destination = when {
        isLanguageSelected && isOnboardingCompleted -> R.id.action_splashFragment_to_homeFragment
        isLanguageSelected && !isOnboardingCompleted -> R.id.action_splashFragment_to_onboardFragment
        else -> R.id.action_splashFragment_to_languageFragment
    }
    
    // Nếu quay về từ màn hình chọn ngôn ngữ thì đi thẳng vào Home
    if (isCameFromLanguage()) {
        navigator.navigateTo(R.id.action_splashFragment_to_homeFragment)
    } else {
        navigator.navigateTo(destination)
    }
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
