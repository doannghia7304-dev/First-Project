package pion.tech.pionbase.feature.splash

import android.animation.ValueAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pion.tech.pionbase.R
import pion.tech.pionbase.base.launchIO

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
            duration = 15000

            addUpdateListener { animation ->
                runCatching {
                    binding.progressBar.progress = animation.animatedValue as Int
                }
            }
            start()
        }
}

fun SplashFragment.goToNextScreen() {
    launchIO {
        val destination =
            if (isPremiumValue() || isCameFromLanguage()) {
                R.id.action_splashFragment_to_homeFragment
            } else {
                R.id.action_splashFragment_to_languageFragment
            }
        withContext(Dispatchers.Main) {
            navigator.navigateTo(destination)
        }
    }
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
//        spaceNameConfig = "Language1.1",
//        spaceNameAds = "language1_native1",
//    )
//    safePreloadAds(
//        spaceNameConfig = "Language1.1",
//        spaceNameAds = "language1_native2",
//    )
//    safePreloadAds(
//        spaceNameConfig = "Language1.1",
//        spaceNameAds = "language1_native3",
//    )
//    if (AdsConstant.listConfigAds["Language1.1"]?.isOn == true) {
//        safePreloadAds(
//            spaceNameConfig = "Language1.2",
//            spaceNameAds = "language1_native4",
//        )
//        safePreloadAds(
//            spaceNameConfig = "Language1.2",
//            spaceNameAds = "language1_native5",
//        )
//        safePreloadAds(
//            spaceNameConfig = "Language1.2",
//            spaceNameAds = "language1_native6",
//        )
//    }
//    safePreloadAds(
//        spaceNameConfig = "language2.1",
//        spaceNameAds = "language2.1_native",
//    )
//    if (AdsConstant.listConfigAds["language2.1"]?.isOn == true) {
//        safePreloadAds(
//            spaceNameConfig = "language2.2",
//            spaceNameAds = "language2.2_native",
//        )
//    }
}