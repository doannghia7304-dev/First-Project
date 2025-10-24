package pion.datlt.libads.utils.adsuntils

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import pion.datlt.libads.AdsActivity
import pion.datlt.libads.AdsController
import pion.datlt.libads.R
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.DialogLoadAdsUtils
import pion.datlt.libads.utils.StateLoadAd
import pion.datlt.libads.utils.adsuntils.checkConditionShowAds
import pion.datlt.libads.utils.adsuntils.checkIsPreloadAfterShow
import pion.datlt.libads.utils.adsuntils.isAllHigherAdsFailed
import pion.datlt.libads.utils.adsuntils.isConfigType
import pion.datlt.libads.utils.adsuntils.safePreloadAds

fun Fragment.showAdsRewardInterstitial(
    configName: String,
    listSpaceName: List<String>,
    timeout: Long = 7000L,
    destinationToShowAds: Int? = null,
    isShowLoadingView: Boolean = true,
    timeShowLoadingView: Long = 500L,
    isLoadingScreenType: Boolean = false,
    dialogView: View? = LayoutInflater.from(context).inflate(R.layout.dialog_reward_inter, null),
    onCancelDialog: (() -> Unit)? = null,
    onBuyIapDialog: (() -> Unit)? = null,
    onAdsDone: ((isGotReward: Boolean, isOffByCondition: Boolean) -> Unit),
    onGetReward: (() -> Unit)? = null
) {

    val mapResult = LinkedHashMap<String, StateLoadAd>()
    var isTimeOut = false
    var isDialogLoadingDone = !isShowLoadingView
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

    var adsNeedToShow: String? = null
    fun showAds(spaceName: String) {
        if (!isDialogLoadingDone) {
            adsNeedToShow = spaceName
            return
        }
        mapResult[spaceName] = StateLoadAd.HAS_BEEN_OPENED
        var isRewardGot = false
        AdsController.getInstance().showLoadedAds(
            spaceName = spaceName,
            destinationToShowAds = destinationToShowAds,
            lifecycle = lifecycle,
            timeout = timeout,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    DialogLoadAdsUtils.getInstance().hideDialogLoadingAds()
                    AdsController.isInterIsShowing = true

                }

                override fun onAdClose() {
                    AdsController.isInterIsShowing = false
                    AdsController.isBlockOpenAds =
                        fragmentEvent == Lifecycle.Event.ON_STOP
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
                    onAdsDone.invoke(isRewardGot, false)
                }

                override fun onAdFailToLoad(messageError: String?) {
                    DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                    AdsController.isInterIsShowing = false
                    AdsController.isBlockOpenAds =
                        fragmentEvent == Lifecycle.Event.ON_STOP
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
                    onAdsDone.invoke(false, false)
                }

                override fun onAdClick() {
                    AdsController.isBlockOpenAds = true
                    if (activity is AdsActivity) {
                        (activity as AdsActivity).sendNotification()
                    }
                }

                override fun onGotReward() {
                    isRewardGot = true
                    onGetReward?.invoke()
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

    val handler = Handler(Looper.getMainLooper())
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
            onAdsDone.invoke(false, false)
        }
    }
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
            onAdsDone.invoke(false, false)
        } else if (isAllHigherAdsFailed && mapResult[spaceName] == StateLoadAd.SUCCESS) {
            //show luon cai hien tai
            handler.removeCallbacks(timeOutRunnable)
            showAds(spaceName)
        }
    }

    //load truoc quang cao
    fun preloadAds() {
        handler.postDelayed(timeOutRunnable, timeout)
        AdsController.isInterIsShowing = true
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


        if (isShowLoadingView) {
            //show dialog
            DialogLoadAdsUtils.getInstance().showDialogLoadingAds(activity, isLoadingScreenType)
            handlerDialogLoading.postDelayed(timeOutDialogLoadingRunnable, timeShowLoadingView)
        }
    }

    fun showDialog() {
        var countDownTimer: CountDownTimer? = null
        val dialog = Dialog(context!!)
        dialog.setContentView(dialogView!!)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    countDownTimer?.cancel()
                    dialog.dismiss()
                }

                else -> {

                }
            }
        })
        if (!dialog.isShowing) {
            dialog.show()
        }

        dialogView.findViewById<View>(R.id.btnBuyIap)?.let {
            it.setOnClickListener {
                countDownTimer?.cancel()
                dialog.dismiss()
                onBuyIapDialog?.invoke()
            }
        }
        dialogView.findViewById<View>(R.id.btnClose)?.let {
            it.setOnClickListener {
                countDownTimer?.cancel()
                dialog.dismiss()
                onCancelDialog?.invoke()
            }
        }

        //dem nguoc
        countDownTimer = object : CountDownTimer(5000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val timeRemain: Int = (millisUntilFinished / 1000L).toInt() + 1
                dialogView.findViewById<TextView>(R.id.txvCountDown)?.let { txvCountDown ->
                    txvCountDown.visibility = View.VISIBLE
                    txvCountDown.post {
                        txvCountDown.text =
                            "${getString(R.string.ads_show_after)} ${timeRemain}s"
                    }
                }
            }

            override fun onFinish() {
                //show ads
                dialog.dismiss()
                preloadAds()
            }
        }

        countDownTimer.start()
    }

    if (AdsConstant.isPremium) {
        onGetReward?.invoke()
        onAdsDone.invoke(false, false)
    } else {
        if (checkConditionShowAds(context, configName)) {
            if (dialogView != null) {
                //show dialog
                showDialog()
            } else {
                //show luon reward
                preloadAds()
            }
        } else {
            onAdsDone.invoke(false, true)
        }
    }
}