package pion.datlt.libads.utils.adsuntils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import pion.datlt.libads.AdsActivity
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.DialogLoadAdsUtils
import pion.datlt.libads.utils.StateLoadAd

fun Fragment.showAdsOpenApp(
    configName: String,
    listSpaceName: List<String>,
    timeout: Long = 7000L,
    destinationToShowAds : Int? = null,
    navOrBack: () -> Unit,
    onAdsDone: ((isSuccess: Boolean) -> Unit)? = null
){
    if (checkConditionShowAds(context, configName)){
        AdsController.isOtherOpenAdsIsShowing = true
        var fragmentEvent = Lifecycle.Event.ON_ANY
        val lifecycleObserver = LifecycleEventObserver { source, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                fragmentEvent = event
            }
        }
        val callback = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                fragmentEvent = Lifecycle.Event.ON_STOP
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {


            }
        }
        AdsController.getInstance().activity.application.registerActivityLifecycleCallbacks(callback)//lắng nghe Lifecycle của activity nếu show qc mà app phải nhảy vào onstop thì sẽ chuyển màn khi qc được đóng. Dùng cho trường hợp show qc pangle không chuyển màn
        lifecycle.addObserver(lifecycleObserver)//lắng nghe Lifecycle của fragment nếu show qc mà app phải nhảy vào onstop thì sẽ chuyển màn khi qc được đóng. Dùng cho trường hợp show qc pangle không chuyển màn

        val mapResult = LinkedHashMap<String, StateLoadAd>()
        var isTimeOut = false

        fun showAds(spaceName: String) {
            mapResult[spaceName] = StateLoadAd.HAS_BEEN_OPENED
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
                AdsController.isOtherOpenAdsIsShowing = false
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
                AdsController.isOtherOpenAdsIsShowing = false
                navOrBack.invoke()
                onAdsDone?.invoke(false)
            } else if (isAllHigherAdsFailed && mapResult[spaceName] == StateLoadAd.SUCCESS) {
                //show luon cai hien tai
                handler.removeCallbacks(timeOutRunnable)
                showAds(spaceName)
            }
        }

        listSpaceName.forEach{spaceName ->
            mapResult[spaceName] = StateLoadAd.NONE
        }
        //load truoc quang cao
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

    }else{
        navOrBack.invoke()
        onAdsDone?.invoke(false)
    }
}