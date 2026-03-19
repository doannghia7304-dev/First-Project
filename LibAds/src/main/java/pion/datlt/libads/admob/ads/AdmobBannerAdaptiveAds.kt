package pion.datlt.libads.admob.ads

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
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
import java.util.*

class AdmobBannerAdaptiveAds : AdmobAds() {

    private var adView: AdView? = null
    private var mAdCallback: AdCallback? = null
    private var mPreloadCallback: PreloadCallback? = null
    private var adSize: AdSize? = null

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
        mAdCallback = adCallback
        if (stateLoadAd != StateLoadAd.LOADING) {
            load(
                activity = activity,
                adsChild = adsChild,
                isPreload = false,
                widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                loadCallback = object : PreloadCallback {
                    override fun onLoadDone() {
                        show(
                            activity = activity,
                            adsChild = adsChild,
                            destinationToShowAds = destinationToShowAds,
                            viewGroupAds = viewGroupAds,
                            viewAds = viewAds,
                            lifecycle = lifecycle,
                            adCallback = adCallback,
                            timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                        )
                    }

                    override fun onLoadFail(error: String) {
                        super.onLoadFail(error)
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
            widthBannerAdaptiveAds = widthBannerAdaptiveAds,
            isPreload = true
        )
    }

    private fun load(
        activity: Activity,
        adsChild: AdsChild,
        isPreload: Boolean,
        loadCallback: PreloadCallback? = null,
        widthBannerAdaptiveAds: Int? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            stateLoadAd = StateLoadAd.LOADING
            Log.d(
                "TESTERADSEVENT",
                "start load banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
            )
            val idAds =
                if (AdsConstant.isDebug) AdsConstant.ID_ADMOB_BANNER_ADAPTIVE_TEST else adsChild.adsId
            adView = AdView(activity.applicationContext)
            adView?.setBackgroundColor(Color.WHITE)
            adView?.adUnitId = idAds
            adSize = getAdsize(activity, widthBannerAdaptiveAds)
            adView?.setAdSize(adSize!!)

            adView?.adListener = object : AdListener() {

                override fun onAdOpened() {
                    super.onAdOpened()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d(
                        "TESTERADSEVENT",
                        "click banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                    )
                    mAdCallback?.onAdClick()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    mAdCallback?.onAdClose()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    stateLoadAd = StateLoadAd.LOAD_FAILED
                    Log.d(
                        "TESTERADSEVENT",
                        "load failed banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ${error.message}"
                    )
                    mAdCallback?.onAdFailToLoad(error.message)
                    loadCallback?.onLoadFail(error.message)
                    if (isPreload) {
                        mPreloadCallback?.onLoadFail(error.message)
                    }
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    timeLoader = Date().time
                    adView?.let {
                        it.responseInfo?.adapterResponses?.forEach { responseInfo ->
                            if (responseInfo.adSourceId.isNotEmpty()) {
                                adSourceId = responseInfo.adSourceId
                            }
                            if (responseInfo.adSourceName.isNotEmpty()) {
                                adSourceName = responseInfo.adSourceName
                            }
                        }
                        adUnitId = it.adUnitId
                    }
                    stateLoadAd = StateLoadAd.SUCCESS
                    Log.d(
                        "TESTERADSEVENT",
                        "load success banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                    )
                    loadCallback?.onLoadDone()
                    if (isPreload) {
                        mPreloadCallback?.onLoadDone()
                    }
                }
            }

            adView?.onPaidEventListener = OnPaidEventListener { adValue ->
                val bundle = Bundle().apply {
                    putString("ad_unit_id", adUnitId)
                    putInt("precision_type", adValue.precisionType)
                    putLong("revenue_micros", adValue.valueMicros)
                    putString("ad_source_id", adSourceId)
                    putString("ad_source_name", adSourceName)
                    putString("ad_type", AdDef.ADS_TYPE_ADMOB.BANNER_ADAPTIVE)
                    putString("currency_code", adValue.currencyCode)
                }
                mAdCallback?.onPaidEvent(bundle)
            }

            withContext(Dispatchers.Main) {
                adView?.loadAd(
                    AdRequest.Builder().build()
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
        mAdCallback = adCallback

        viewGroupAds?.let { viewG ->
            val lp = viewG.layoutParams
            lp.width = adSize?.getWidthInPixels(viewG.context) ?: 0
            lp.height = adSize?.getHeightInPixels(viewG.context) ?: 0
            viewG.layoutParams = lp
        }

        if (adView != null && viewGroupAds != null) {
            if (destinationToShowAds != null && destinationToShowAds != AdsController.currentDestinationId) {
                Log.d(
                    "TESTERADSEVENT",
                    "show failed banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : show in wrong destination"
                )
                adCallback?.onAdFailToLoad("show in wrong destination")
            } else if (!wasLoadTimeLessThanNHoursAgo()) {
                stateLoadAd = StateLoadAd.SHOW_FAILED
                Log.d(
                    "TESTERADSEVENT",
                    "show failed banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ads expired"
                )
                adCallback?.onAdFailToLoad("ads expired")
            } else {
                viewGroupAds.removeAllViews()
                if (adView!!.parent != null) {
                    (adView!!.parent as ViewGroup).removeView(adView)
                }
                viewGroupAds.visibility = View.VISIBLE
                viewGroupAds.addView(adView)
                stateLoadAd = StateLoadAd.HAS_BEEN_OPENED
                Log.d(
                    "TESTERADSEVENT",
                    "show success banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                )
                mAdCallback?.onAdShow()
            }
        } else {
            Log.d(
                "TESTERADSEVENT",
                "show failed banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : layout null"
            )
            adCallback?.onAdFailToLoad("layout null")
        }
    }

    override fun setPreloadCallback(preloadCallback: PreloadCallback?) {
        mPreloadCallback = preloadCallback
    }

    override fun removePreloadCallback() {
        mPreloadCallback = null
    }


    private fun getAdsize(activity: Activity, widthBannerAdaptiveAds: Int?): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        val adWidth = if (widthBannerAdaptiveAds != null) {
            (widthBannerAdaptiveAds / density).toInt()
        } else {
            val widthPixels = outMetrics.widthPixels.toFloat()
            (widthPixels / density).toInt()
        }
        Log.d("CHECKWIDTHBANNER", "getAdsize: $adWidth $widthBannerAdaptiveAds")
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            activity,
            adWidth
        )
    }

    override fun destroyAds() {
        adView = null
        mAdCallback = null
        mPreloadCallback = null
        adSize = null
        stateLoadAd = StateLoadAd.NULL
    }
}