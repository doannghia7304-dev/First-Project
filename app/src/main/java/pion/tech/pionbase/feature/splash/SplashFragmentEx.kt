package pion.tech.pionbase.feature.splash

import android.animation.ValueAnimator
import pion.tech.pionbase.R

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

    progressAnimator =
        ValueAnimator.ofInt(0, 100).apply {
            duration = 15000L

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

    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        goToNextScreen()
    }, 3000L)
}

fun SplashFragment.preloadLanguageAds() {
    if (isCameFromLanguage()) return
//    safePreloadAds(
//        configName = LanguageAds.NATIVE_11_CONFIG,
//        spaceName = LanguageAds.NATIVE_11_SPACE1,
//    )
//    safePreloadAds(
//        configName = LanguageAds.NATIVE_11_CONFIG,
//        spaceName = LanguageAds.NATIVE_11_SPACE2,
//    )
//
//    if (AdsConstant.listConfigAds[LanguageAds.NATIVE_11_CONFIG]?.isOn == true) {
//        safePreloadAds(
//            configName = LanguageAds.NATIVE_12_CONFIG,
//            spaceName = LanguageAds.NATIVE_12_SPACE1,
//        )
//        safePreloadAds(
//            configName = LanguageAds.NATIVE_12_CONFIG,
//            spaceName = LanguageAds.NATIVE_12_SPACE2,
//        )
//    }
}
