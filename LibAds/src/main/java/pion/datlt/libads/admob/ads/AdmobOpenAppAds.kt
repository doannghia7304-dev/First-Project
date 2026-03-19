package pion.datlt.libads.admob.ads

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.appopen.AppOpenAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.AdsChild
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.StateLoadAd
import java.util.Date

class AdmobOpenAppAds : AdmobAds() {

    private var appOpenAd: AppOpenAd? = null
    private var isTimeOut: Boolean = false

    private var eventLifecycle: Lifecycle.Event = Lifecycle.Event.ON_RESUME

    private var mActivity: Activity? = null
    private var mAdsChild: AdsChild? = null

    private var mDestinationToShowAds: Int? = null

    private var mPreloadCallback: PreloadCallback? = null
    private var mAdCallback: AdCallback? = null

    private val handler = Handler(Looper.getMainLooper())

    private var mLifecycle: Lifecycle? = null

    private val countDownShowAds = object : CountDownTimer(4000L, 4000L) {
        override fun onTick(p0: Long) {

        }

        override fun onFinish() {
            appOpenAd = null
            stateLoadAd = StateLoadAd.SHOW_FAILED
            if (eventLifecycle == Lifecycle.Event.ON_RESUME) {
                mLifecycle?.removeObserver(lifecycleObserver)
                Log.d(
                    "TESTERADSEVENT",
                    "show failed open app : ads name ${mAdsChild?.spaceName} id ${mAdsChild?.adsId} error : timeout show ads"
                )
                mAdCallback?.onAdFailToLoad("timeout show ads")
            }
        }

    }

    private val timeoutCallback = Runnable {
        if (
            stateLoadAd != StateLoadAd.SUCCESS
            && stateLoadAd != StateLoadAd.LOAD_FAILED
            && stateLoadAd != StateLoadAd.SHOW_FAILED
        ) {
            //none hoac loading
            isTimeOut = true
            if (eventLifecycle == Lifecycle.Event.ON_RESUME) {
                mLifecycle?.removeObserver(lifecycleObserver)
                Log.d(
                    "TESTERADSEVENT",
                    "show failed open app : ads name ${mAdsChild?.spaceName} id ${mAdsChild?.adsId} error : time out load ads"
                )
                mAdCallback?.onAdFailToLoad("time out load ads")
            }
        }
    }

    private val lifecycleObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            eventLifecycle = event
            if (event == Lifecycle.Event.ON_RESUME) {
                if (stateLoadAd == StateLoadAd.SUCCESS) {
                    if (mActivity != null && mAdsChild != null) {
                        show(
                            activity = mActivity!!,
                            adsChild = mAdsChild!!,
                            destinationToShowAds = mDestinationToShowAds,
                            adCallback = mAdCallback,
                            lifecycle = mLifecycle,
                            viewGroupAds = null,
                            viewAds = null,
                            timeShowNativeCollapsibleAfterClose = 0
                        )
                    } else {
                        mLifecycle?.removeObserver(this)
                        Log.d(
                            "TESTERADSEVENT",
                            "show failed open app : ads name ${mAdsChild?.spaceName} id ${mAdsChild?.adsId} error : activity or adsChild must not null"
                        )
                        mAdCallback?.onAdFailToLoad("activity or adsChild must not null")
                    }
                } else {
                    mLifecycle?.removeObserver(this)
                    Log.d(
                        "TESTERADSEVENT",
                        "show failed open app : ads name ${mAdsChild?.spaceName} id ${mAdsChild?.adsId} error : StateLoadAd not success when resume"
                    )
                    mAdCallback?.onAdFailToLoad("StateLoadAd not success when resume")
                }
            }
        }
    }

    override fun loadAndShow(
        activity: Activity,
        adsChild: AdsChild,
        destinationToShowAds: Int?,
        adCallback: AdCallback?,
        lifecycle: Lifecycle?,
        timeout: Long?,
        viewGroupAds: ViewGroup?,
        viewAds: View?,
        adChoice: Int?,
        positionCollapsibleBanner: String?,
        isOneTimeCollapsible: Boolean?,
        widthBannerAdaptiveAds: Int?,
        timeShowNativeCollapsibleAfterClose: Int?
    ) {

        mActivity = activity
        mAdsChild = adsChild
        mDestinationToShowAds = destinationToShowAds
        mAdCallback = adCallback
        mLifecycle = lifecycle

        if (stateLoadAd == StateLoadAd.SUCCESS) {
            show(
                activity = activity,
                adsChild = adsChild,
                destinationToShowAds = destinationToShowAds,
                adCallback = adCallback,
                lifecycle = lifecycle,
                viewGroupAds = viewGroupAds,
                viewAds = viewAds,
                timeShowNativeCollapsibleAfterClose = 0
            )
        } else if (stateLoadAd != StateLoadAd.LOADING) {
            load(
                activity = activity,
                adsChild = adsChild,
                isPreload = false,
                loadCallback = object : PreloadCallback {
                    override fun onLoadDone() {
                        if (isTimeOut) {
                            //khong show nua
                            //reset timeout
                            isTimeOut = false //reset khi load qua thoi gian
                        } else {
                            //show luon
                            show(
                                activity = activity,
                                adsChild = adsChild,
                                destinationToShowAds = destinationToShowAds,
                                adCallback = adCallback,
                                lifecycle = lifecycle,
                                viewGroupAds = viewGroupAds,
                                viewAds = viewAds,
                                timeShowNativeCollapsibleAfterClose = 0
                            )
                        }
                    }

                    override fun onLoadFail(error: String) {
                        adCallback?.onAdFailToLoad(error)
                    }
                }
            )
        }
    }

    override fun preload(
        activity: Activity,
        adsChild: AdsChild,
        positionCollapsibleBanner: String?,
        adChoice: Int?,
        isOneTimeCollapsible: Boolean?,
        widthBannerAdaptiveAds: Int?
    ) {
        load(
            activity = activity,
            adsChild = adsChild,
            isPreload = true
        )
    }

    private fun load(
        activity: Activity,
        adsChild: AdsChild,
        isPreload: Boolean,
        timeout: Long = AdsConstant.TIME_OUT_DEFAULT,
        loadCallback: PreloadCallback? = null
    ) {


        CoroutineScope(Dispatchers.IO).launch {
            stateLoadAd = StateLoadAd.LOADING
            Log.d(
                "TESTERADSEVENT",
                "start load open app : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
            )
            mActivity = activity
            mAdsChild = adsChild
            isTimeOut = false
            val id = if (AdsConstant.isDebug) AdsConstant.ID_ADMOB_APP_OPEN_TEST else adsChild.adsId
            if (!isPreload) {
                handler.removeCallbacks(timeoutCallback)
                handler.postDelayed(timeoutCallback, timeout)
            }

            val appOpenCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ads: AppOpenAd) {
                    super.onAdLoaded(ads)
                    appOpenAd = ads
                    timeLoader = Date().time
                    stateLoadAd = StateLoadAd.SUCCESS
                    Log.d(
                        "TESTERADSEVENT",
                        "load success open app : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                    )
                    loadCallback?.onLoadDone()
                    handler.removeCallbacks(timeoutCallback)
                    if (isPreload) {
                        mPreloadCallback?.onLoadDone()
                    }


                    appOpenAd?.let {
                        it.responseInfo.adapterResponses.forEach { responseInfo ->
                            if (responseInfo.adSourceId.isNotEmpty()) {
                                adSourceId = responseInfo.adSourceId
                            }
                            if (responseInfo.adSourceName.isNotEmpty()) {
                                adSourceName = responseInfo.adSourceName
                            }
                        }
                        adUnitId = it.adUnitId
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    handler.removeCallbacks(timeoutCallback)
                    stateLoadAd = StateLoadAd.LOAD_FAILED
                    Log.d(
                        "TESTERADSEVENT",
                        "load failed open app : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ${error.message}"
                    )
                    loadCallback?.onLoadFail(error.message)
                    if (isPreload) {
                        mPreloadCallback?.onLoadFail(error.message)
                    }
                }
            }
            val request = AdRequest.Builder().build()

            withContext(Dispatchers.Main) {
                AppOpenAd.load(
                    activity, id, request, appOpenCallback
                )
            }
        }


    }

    override fun show(
        activity: Activity,
        adsChild: AdsChild,
        destinationToShowAds: Int?,
        adCallback: AdCallback?,
        lifecycle: Lifecycle?,
        viewGroupAds: ViewGroup?,
        viewAds: View?,
        timeShowNativeCollapsibleAfterClose: Int?
    ) {
        mActivity = activity
        mAdsChild = adsChild
        mDestinationToShowAds = destinationToShowAds
        mAdCallback = adCallback
        mLifecycle = lifecycle

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                lifecycle?.removeObserver(lifecycleObserver)
                countDownShowAds.cancel()
                appOpenAd = null
                Log.d(
                    "TESTERADSEVENT",
                    "close open app : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                )
                adCallback?.onAdClose()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                //goi khi quang cao duoc show len
                lifecycle?.removeObserver(lifecycleObserver)
                countDownShowAds.cancel()
                appOpenAd = null
                stateLoadAd = StateLoadAd.HAS_BEEN_OPENED
                Log.d(
                    "TESTERADSEVENT",
                    "show success open app : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                )
                adCallback?.onAdShow()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                countDownShowAds.cancel()
                appOpenAd = null
                stateLoadAd = StateLoadAd.SHOW_FAILED
                Log.d(
                    "TESTERADSEVENT",
                    "show failed open app : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ${adError.message}"
                )
                if (eventLifecycle == Lifecycle.Event.ON_RESUME) {
                    lifecycle?.removeObserver(lifecycleObserver)
                    adCallback?.onAdFailToLoad(adError.message)
                }
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(
                    "TESTERADSEVENT",
                    "click open app : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                )
                adCallback?.onAdClick()
            }


            override fun onAdImpression() {
                super.onAdImpression()
            }

        }

        appOpenAd?.onPaidEventListener = OnPaidEventListener { adValue ->
            val bundle = Bundle().apply {
                putString("ad_unit_id", adUnitId)
                putInt("precision_type", adValue.precisionType)
                putLong("revenue_micros", adValue.valueMicros)
                putString("ad_source_id", adSourceId)
                putString("ad_source_name", adSourceName)
                putString("ad_type", AdDef.ADS_TYPE_ADMOB.OPEN_APP)
                putString("currency_code", adValue.currencyCode)
            }
            adCallback?.onPaidEvent(bundle)
        }

        lifecycle?.let {
            if (lifecycle.currentState != Lifecycle.State.RESUMED) {
                lifecycle.removeObserver(lifecycleObserver)
                lifecycle.addObserver(lifecycleObserver)
            }
        }

        if (eventLifecycle == Lifecycle.Event.ON_RESUME && !isTimeOut) {
            if (mDestinationToShowAds != null && mDestinationToShowAds != AdsController.currentDestinationId) {
                Log.d(
                    "TESTERADSEVENT",
                    "show failed open app : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : show in wrong destination"
                )
                adCallback?.onAdFailToLoad("show in wrong destination")
            } else if (!wasLoadTimeLessThanNHoursAgo()) {
                stateLoadAd = StateLoadAd.SHOW_FAILED
                Log.d(
                    "TESTERADSEVENT",
                    "show failed open app : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ads expired"
                )
                adCallback?.onAdFailToLoad("ads expired")
            } else {
                appOpenAd?.show(activity)
                //bat dau dem nguoc
                countDownShowAds.start()
            }
        }
    }

    override fun setPreloadCallback(preloadCallback: PreloadCallback?) {
        mPreloadCallback = preloadCallback
    }


    override fun removePreloadCallback() {
        mPreloadCallback = null
    }

    override fun destroyAds() {
        appOpenAd = null
        isTimeOut = false
        eventLifecycle = Lifecycle.Event.ON_RESUME
        mActivity = null
        mAdsChild = null
        mDestinationToShowAds = null
        mPreloadCallback = null
        mAdCallback = null
        mLifecycle = null
        stateLoadAd = StateLoadAd.NULL
    }
}