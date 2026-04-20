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
    //TODO : check logic AdsConstant.isPremium
//    val destination =
//        if (AdsConstant.isPremium || isCameFromLanguage()) {
//            R.id.action_splashFragment_to_homeFragment
//        } else {
//            R.id.action_splashFragment_to_languageFragment
//        }
    val destination =
        if (isCameFromLanguage()) {
            R.id.action_splashFragment_to_homeFragment
        } else {
            R.id.action_splashFragment_to_languageFragment
        }
    navigator.navigateTo(destination)
}

fun SplashFragment.isCameFromLanguage(): Boolean = navigator.isCameFrom(R.id.languageFragment)

fun SplashFragment.showAds() {
//    safePreloadAds(
//        spaceNameConfig = "Splash",
//        spaceNameAds = "Splash_Interstitial2",
//    )
//    safePreloadAds(
//        spaceNameConfig = "Splash",
//        spaceNameAds = "Splash_Interstitial3",
//    )
//    safePreloadAds(
//        spaceNameConfig = "Splash",
//        spaceNameAds = "Splash_Openad",
//    )
//    preloadLanguageAds()
//    var isTimeOut = false
//    val handler = Handler(Looper.getMainLooper())
//    val timeOutRunnable =
//        Runnable {
//            isTimeOut = true
//            goToNextScreen()
//        }
//    handler.postDelayed(timeOutRunnable, 15000L)
//    showSplashInter(
//        spaceNameConfig = "Splash",
//        spaceNameInter1 = "Splash_Interstitial2",
//        spaceNameInter2 = "Splash_Interstitial3",
//        spaceNameOpenAds = "Splash_Openad",
//        timeOut = 15000L,
//        destinationToShowAds = R.id.splashFragment,
//        navOrBack = {
//            if (!isTimeOut) {
//                handler.removeCallbacks(timeOutRunnable)
//                goToNextScreen()
//            }
//        },
//    )

    goToNextScreen()
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
