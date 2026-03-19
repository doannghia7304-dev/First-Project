package pion.datlt.libads.utils.adsuntils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import pion.datlt.libads.AdsActivity
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.DialogLoadAdsUtils
import pion.datlt.libads.utils.StateLoadAd

fun Fragment.showAdsInterstitial(
    configName: String,
    listSpaceName: List<String>,
    timeout: Long = 7000L,
    destinationToShowAds: Int? = null,
    isShowLoadingView: Boolean = true,
    timeShowLoadingView: Long = 500L,
    isLoadingScreenType: Boolean = false,
    navOrBack: () -> Unit,
    onAdsDone: ((isSuccess: Boolean) -> Unit)? = null
) {
    if (checkConditionShowAds(context, configName)) {
        AdsController.isInterIsShowing = true
        //theo doi lifecycle event
        var fragmentEvent = Lifecycle.Event.ON_ANY
        val callback = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d("CHECKINTERLOGIC", "onActivityCreated: ")
            }

            override fun onActivityStarted(activity: Activity) {
                Log.d("CHECKINTERLOGIC", "onActivityStarted: ")
            }

            override fun onActivityResumed(activity: Activity) {
                Log.d("CHECKINTERLOGIC", "onActivityResumed: ")
            }

            override fun onActivityPaused(activity: Activity) {
                Log.d("CHECKINTERLOGIC", "onActivityPaused: ")
            }

            override fun onActivityStopped(activity: Activity) {
                fragmentEvent = Lifecycle.Event.ON_STOP
                Log.d("CHECKINTERLOGIC", "onActivityStopped: ")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.d("CHECKINTERLOGIC", "onActivitySaveInstanceState: ")
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.d("CHECKINTERLOGIC", "onActivityDestroyed: ")
            }
        }
        AdsController.getInstance().activity.application.registerActivityLifecycleCallbacks(callback)
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                fragmentEvent = event
            }
        }

        val mapResult = LinkedHashMap<String, StateLoadAd>()
        var isTimeOut = false
        var isDialogLoadingDone = !isShowLoadingView

        var adsNeedToShow: String? = null
        fun showAds(spaceName: String) {
            if (!isDialogLoadingDone) {
                adsNeedToShow = spaceName
                return
            }
            mapResult[spaceName] = StateLoadAd.HAS_BEEN_OPENED
            //show ads
            var isShowSuccess = false
            AdsController.getInstance().showLoadedAds(
                spaceName = spaceName,
                destinationToShowAds = destinationToShowAds,
                lifecycle = lifecycle,
                timeout = timeout,
                adCallback = object : AdCallback {
                    override fun onAdShow() {
                        isShowSuccess = true
                        if (AdsConstant.listConfigAds[configName]?.isShowNativeAfterInter == true) {
                            AdsController.getInstance().showNativeTrigger?.invoke()
                        }
                        DialogLoadAdsUtils.getInstance().hideDialogLoadingAds()
                        AdsController.isInterIsShowing = true
                        setLastTimeShowInter()
                        navOrBack.invoke()
                    }

                    override fun onAdClose() {
                        AdsController.isInterIsShowing = false
                        AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                        AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                            callback
                        )
                        lifecycle.removeObserver(lifecycleObserver)
                        setLastTimeShowInter()
                        if (checkIsPreloadAfterShow(spaceNameConfig = configName)) {
                            safePreloadAds(
                                configName = spaceName,
                                spaceName = configName
                            )
                        }
                        onAdsDone?.invoke(isShowSuccess)
                    }

                    override fun onAdFailToLoad(messageError: String?) {
                        DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                        AdsController.isInterIsShowing = false
                        AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                        AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                            callback
                        )
                        lifecycle.removeObserver(lifecycleObserver)
                        if (checkIsPreloadAfterShow(spaceNameConfig = configName)) {
                            safePreloadAds(
                                configName = spaceName,
                                spaceName = configName
                            )
                        }
                        navOrBack.invoke()
                        onAdsDone?.invoke(false)
                    }

                    override fun onAdClick() {
                        super.onAdClick()
                        AdsController.isBlockOpenAds = true
                        if (activity is AdsActivity) {
                            (activity as AdsActivity).sendNotification()
                        }
                    }
                }
            )
        }

        fun showAnySuccess() {
            for (result in mapResult) {
                if (result.value == StateLoadAd.SUCCESS) {
                    showAds(result.key)
                    break
                }
            }
        }

        val timeOutRunnable = Runnable {
            isTimeOut = true
            if (mapResult.any { it.value == StateLoadAd.HAS_BEEN_OPENED }) return@Runnable
            val isAnySuccess = mapResult.values.any { it == StateLoadAd.SUCCESS }
            if (isAnySuccess) {
                //thanh cong cai nao show cai do
                showAnySuccess()
            } else {
                //co quang cao van dang duoc loading hoac load failed
                //cho phep chuyen man, hoac thuc hien hanh dong
                AdsController.isInterIsShowing = false
                DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                navOrBack.invoke()
                onAdsDone?.invoke(false)
            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(timeOutRunnable, timeout)
        val handlerDialogLoading = Handler(Looper.getMainLooper())
        val timeOutDialogLoadingRunnable = Runnable {
            isDialogLoadingDone = true
            adsNeedToShow?.let {
                showAds(it)
            }
        }

        fun checkShowAds(spaceName: String) {
            if (isTimeOut) return
            if (mapResult.any { it.value == StateLoadAd.HAS_BEEN_OPENED }) return
            val isAllFailed = mapResult.values.all { it == StateLoadAd.LOAD_FAILED }
            val isAllHigherAdsFailed = mapResult.isAllHigherAdsFailed(spaceName)
            if (isAllFailed) {
                //khong show
                //thuc hien hanh dong tiep theo
                handler.removeCallbacks(timeOutRunnable)
                AdsController.isInterIsShowing = false
                DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                navOrBack.invoke()
                onAdsDone?.invoke(false)
            } else if (isAllHigherAdsFailed && mapResult[spaceName] == StateLoadAd.SUCCESS) {
                //show luon cai hien tai
                handler.removeCallbacks(timeOutRunnable)
                showAds(spaceName)
            }
        }

        //load truoc quang cao
        listSpaceName.forEach{spaceName ->
            mapResult[spaceName] = StateLoadAd.NONE
        }
        listSpaceName.forEach { spaceName ->
            mapResult[spaceName] = StateLoadAd.LOADING
            safePreloadAds(
                configName = configName,
                spaceName = spaceName,
                preloadCallback = object : PreloadCallback {
                    override fun onLoadDone() {
                        mapResult[spaceName] = StateLoadAd.SUCCESS
                        checkShowAds(spaceName)
                    }

                    override fun onLoadFail(error: String) {
                        super.onLoadFail(error)
                        mapResult[spaceName] = StateLoadAd.LOAD_FAILED
                        checkShowAds(spaceName)
                    }
                }
            )
        }

        if (isShowLoadingView) {
            //show dialog
            DialogLoadAdsUtils.getInstance().showDialogLoadingAds(activity, isLoadingScreenType)
            handlerDialogLoading.postDelayed(timeOutDialogLoadingRunnable, timeShowLoadingView)
        }
    } else {
        navOrBack.invoke()
        onAdsDone?.invoke(false)
    }
}


fun Fragment.showAdsSplash(
    configName: String,
    listSpaceName: List<String>,
    timeout: Long = 15000L,
    destinationToShowAds: Int? = null,
    navOrBack: () -> Unit,
    onAdsDone: ((isSuccess: Boolean) -> Unit)?
) {
    if (checkConditionShowAds(context, configName)) {
        AdsController.isInterIsShowing = true
        AdsController.isOtherOpenAdsIsShowing = true
        //theo doi lifecycle event
        var fragmentEvent = Lifecycle.Event.ON_ANY
        val callback = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d("CHECKINTERLOGIC", "onActivityCreated: ")
            }

            override fun onActivityStarted(activity: Activity) {
                Log.d("CHECKINTERLOGIC", "onActivityStarted: ")
            }

            override fun onActivityResumed(activity: Activity) {
                Log.d("CHECKINTERLOGIC", "onActivityResumed: ")
            }

            override fun onActivityPaused(activity: Activity) {
                Log.d("CHECKINTERLOGIC", "onActivityPaused: ")
            }

            override fun onActivityStopped(activity: Activity) {
                fragmentEvent = Lifecycle.Event.ON_STOP
                Log.d("CHECKINTERLOGIC", "onActivityStopped: ")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.d("CHECKINTERLOGIC", "onActivitySaveInstanceState: ")
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.d("CHECKINTERLOGIC", "onActivityDestroyed: ")
            }
        }
        AdsController.getInstance().activity.application.registerActivityLifecycleCallbacks(callback)
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                fragmentEvent = event
            }
        }

        val mapResult = LinkedHashMap<String, StateLoadAd>()
        var isTimeOut = false

        fun showInterstitial(spaceName: String) {
            var isShowSuccess = false
            AdsController.getInstance().showLoadedAds(
                spaceName = spaceName,
                destinationToShowAds = destinationToShowAds,
                lifecycle = lifecycle,
                timeout = timeout,
                adCallback = object : AdCallback {
                    override fun onAdShow() {
                        isShowSuccess = true
                        if (AdsConstant.listConfigAds[configName]?.isShowNativeAfterInter == true) {
                            AdsController.getInstance().showNativeTrigger?.invoke()
                        }
                        DialogLoadAdsUtils.getInstance().hideDialogLoadingAds()
                        AdsController.isInterIsShowing = true
                        setLastTimeShowInter()
                        navOrBack.invoke()
                    }

                    override fun onAdClose() {
                        AdsController.isInterIsShowing = false
                        AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                        AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                            callback
                        )
                        lifecycle.removeObserver(lifecycleObserver)
                        setLastTimeShowInter()
                        if (checkIsPreloadAfterShow(spaceNameConfig = configName)) {
                            safePreloadAds(
                                configName = spaceName,
                                spaceName = configName
                            )
                        }
                        navOrBack.invoke()
                        onAdsDone?.invoke(isShowSuccess)
                    }

                    override fun onAdFailToLoad(messageError: String?) {
                        DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                        AdsController.isInterIsShowing = false
                        AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                        AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                            callback
                        )
                        lifecycle.removeObserver(lifecycleObserver)
                        if (checkIsPreloadAfterShow(spaceNameConfig = configName)) {
                            safePreloadAds(
                                configName = spaceName,
                                spaceName = configName
                            )
                        }
                        navOrBack.invoke()
                        onAdsDone?.invoke(false)
                    }

                    override fun onAdClick() {
                        super.onAdClick()
                        AdsController.isBlockOpenAds = true
                        if (activity is AdsActivity) {
                            (activity as AdsActivity).sendNotification()
                        }
                    }
                }
            )
        }

        fun showOpenApp(spaceName: String) {
            var isShowSuccess = false
            AdsController.getInstance().showLoadedAds(
                spaceName = spaceName,
                destinationToShowAds = destinationToShowAds,
                lifecycle = lifecycle,
                timeout = timeout,
                adCallback = object : AdCallback {
                    override fun onAdShow() {
                        isShowSuccess = true
                        AdsController.isOtherOpenAdsIsShowing = true
                    }

                    override fun onAdClose() {
                        AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                        AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                            callback
                        )
                        lifecycle.removeObserver(lifecycleObserver)
                        if (checkIsPreloadAfterShow(spaceNameConfig = configName)) {
                            safePreloadAds(
                                configName = spaceName,
                                spaceName = configName
                            )
                        }
                        AdsController.isOtherOpenAdsIsShowing = false
                        setLastTimeShowInter()
                        navOrBack.invoke()
                        onAdsDone?.invoke(isShowSuccess)
                    }

                    override fun onAdFailToLoad(messageError: String?) {
                        AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                        AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                            callback
                        )
                        lifecycle.removeObserver(lifecycleObserver)
                        if (checkIsPreloadAfterShow(spaceNameConfig = configName)) {
                            safePreloadAds(
                                configName = spaceName,
                                spaceName = configName
                            )
                        }
                        AdsController.isOtherOpenAdsIsShowing = false
                        navOrBack.invoke()
                        onAdsDone?.invoke(false)
                    }

                    override fun onAdClick() {
                        AdsController.isBlockOpenAds = true
                        if (activity is AdsActivity){
                            (activity as AdsActivity).sendNotification()
                        }
                    }

                }
            )
        }

        fun showAds(spaceName: String) {
            mapResult[spaceName] = StateLoadAd.HAS_BEEN_OPENED
            when (AdsController.getInstance().getAdsDetail(spaceName)?.adsType) {
                AdDef.ADS_TYPE_ADMOB.INTERSTITIAL -> {
                    AdsController.isInterIsShowing = true
                    AdsController.isOtherOpenAdsIsShowing = false
                    showInterstitial(spaceName)
                }
                AdDef.ADS_TYPE_ADMOB.OPEN_APP ->{
                    AdsController.isInterIsShowing = false
                    AdsController.isOtherOpenAdsIsShowing = true
                    showOpenApp(spaceName)
                }
                else -> {
                    AdsController.isInterIsShowing = false
                    AdsController.isOtherOpenAdsIsShowing = false
                    navOrBack.invoke()
                    onAdsDone?.invoke(false)
                }
            }
        }

        fun showAnySuccess() {
            for (result in mapResult) {
                if (result.value == StateLoadAd.SUCCESS) {
                    showAds(result.key)
                    break
                }
            }
        }

        val timeOutRunnable = Runnable {
            isTimeOut = true
            if (mapResult.any { it.value == StateLoadAd.HAS_BEEN_OPENED }) return@Runnable
            val isAnySuccess = mapResult.values.any { it == StateLoadAd.SUCCESS }
            if (isAnySuccess) {
                //thanh cong cai nao show cai do
                showAnySuccess()
            } else {
                //co quang cao van dang duoc loading hoac load failed
                //cho phep chuyen man, hoac thuc hien hanh dong
                AdsController.isInterIsShowing = false
                AdsController.isOtherOpenAdsIsShowing = false
                DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                navOrBack.invoke()
                onAdsDone?.invoke(false)
            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(timeOutRunnable, timeout)

        fun checkShowAds(spaceName: String) {
            if (isTimeOut) return
            if (mapResult.any { it.value == StateLoadAd.HAS_BEEN_OPENED }) return
            val isAllFailed = mapResult.values.all { it == StateLoadAd.LOAD_FAILED }
            val isAllHigherAdsFailed = mapResult.isAllHigherAdsFailed(spaceName)
            if (isAllFailed) {
                //khong show
                //thuc hien hanh dong tiep theo
                handler.removeCallbacks(timeOutRunnable)
                AdsController.isInterIsShowing = false
                AdsController.isOtherOpenAdsIsShowing = false
                DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                navOrBack.invoke()
                onAdsDone?.invoke(false)
            } else if (isAllHigherAdsFailed && mapResult[spaceName] == StateLoadAd.SUCCESS) {
                //show luon cai hien tai
                handler.removeCallbacks(timeOutRunnable)
                showAds(spaceName)
            }
        }

        //load truoc quang cao
        listSpaceName.forEach{spaceName ->
            mapResult[spaceName] = StateLoadAd.NONE
        }
        listSpaceName.forEach { spaceName ->
            mapResult[spaceName] = StateLoadAd.LOADING
            safePreloadAds(
                configName = configName,
                spaceName = spaceName,
                preloadCallback = object : PreloadCallback {
                    override fun onLoadDone() {
                        mapResult[spaceName] = StateLoadAd.SUCCESS
                        checkShowAds(spaceName)
                    }

                    override fun onLoadFail(error: String) {
                        super.onLoadFail(error)
                        mapResult[spaceName] = StateLoadAd.LOAD_FAILED
                        checkShowAds(spaceName)
                    }
                }
            )
        }

    } else {
        navOrBack.invoke()
        onAdsDone?.invoke(false)
    }
}