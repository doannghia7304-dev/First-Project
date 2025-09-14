package pion.tech.pionbase.feature.splash

import android.animation.ValueAnimator
import androidx.lifecycle.lifecycleScope
import com.example.libiap.IAPConnector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.withContext
import pion.datlt.libads.AdsController
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.loadAndShowConsentFormIfRequire
import pion.datlt.libads.utils.requestConsentInfoUpdate
import pion.tech.pionbase.R
import pion.tech.pionbase.app.GDPRState
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.data.model.remoteConfig.RemoteConfigDtoModel
import pion.tech.pionbase.util.Constant
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.haveNetworkConnection
import timber.log.Timber

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

fun SplashFragment.initGdpr() {
    commonViewModel.setGdprState(GDPRState.LOADING)
    AdsController.getInstance().requestConsentInfoUpdate(
        onFailed = { _ ->
            commonViewModel.setGdprState(GDPRState.DONE)
        },
        onSuccess = { isRequire, _ ->
            if (isRequire) {
                AdsController.getInstance().loadAndShowConsentFormIfRequire(
                    onConsentError = { _ ->
                        commonViewModel.setGdprState(GDPRState.DONE)
                    },
                    onConsentDone = {
                        commonViewModel.setGdprState(GDPRState.DONE)
                    },
                )
            } else {
                commonViewModel.setGdprState(GDPRState.DONE)
            }
        },
    )
}

fun SplashFragment.observerIapRemoteData() {
    if (isCameFromLanguage()) return
    if (context?.haveNetworkConnection() != true) {
        goToNextScreen()
        return
    }
    val tag = "observerIapRemoteData"
    combine(
        IAPConnector.stateCheckIap,
        commonViewModel.remoteConfigUiState,
        commonViewModel.checkGdprState,
    ) { stateCheckIap, remoteConfigData, checkGdprState ->
        Timber.tag(tag).d("stateCheckIap: $stateCheckIap")
        Timber.tag(tag).d("remoteConfigData: $remoteConfigData")
        Timber.tag(tag).d("checkGdprState: $checkGdprState")
        if (stateCheckIap !in
            listOf(
                IAPConnector.StateCheckIap.DONE,
                IAPConnector.StateCheckIap.FAILED,
            ) ||
            remoteConfigData !is UiState.Success ||
            checkGdprState != GDPRState.DONE
        ) {
            return@combine
        }

        // Update premium status based on IAP check result
        updatePremiumStatus(stateCheckIap == IAPConnector.StateCheckIap.DONE)
        // Process remote config data
        handlerLogicRemoteConfig(remoteConfigData.data)
    }.launchIn(viewLifecycleOwner.lifecycleScope)
}

fun SplashFragment.updatePremiumStatus(isIapCheckSuccessful: Boolean) {
    if (isIapCheckSuccessful) {
        val productModel = IAPConnector.getAllProductModel().find { it.isPurchase }
        val isPremium = productModel?.isPurchase == true
        setPremiumValue(isPremium)
    } else {
        setPremiumValue(false)
    }
}

fun SplashFragment.handlerLogicRemoteConfig(remoteConfigData: RemoteConfigDtoModel?) {
    if (remoteConfigData == null) return
    mapRemoteConfigData(remoteConfigData)
    showAds()
}

fun SplashFragment.mapRemoteConfigData(data: RemoteConfigDtoModel) {
//    runCatching {
//        AdsController.setConfigAds(data.firebaseRemoteConfig.getString("config_show_ads"))
//    }
//    runCatching {
//        AdsController
//            .getInstance()
//            .setListAdsData(listJsonData = arrayListOf(data.firebaseRemoteConfig.getString("admob_id")))
//    }
    Constant.isRemoteConfigSuccess = data.isRealData
//    runCatching {
//        Constant.timeShowDialogChangeLanguage =
//            data.firebaseRemoteConfig.getLong("timeShowDialogChangeLanguage")
//    }.onFailure {
//        Constant.timeShowDialogChangeLanguage = 4000L
//    }
//
//    initNativeFullAfterInter()
}
