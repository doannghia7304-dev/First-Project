package pion.datlt.libads.admob.ads

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.ads.mediation.admob.AdMobAdapter
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

class AdmobBannerCollapsibleAds : AdmobAds() {

    private var adView: AdView? = null
    private var mAdCallback: AdCallback? = null
    private var mPreloadCallback: PreloadCallback? = null
    private var mLifecycle: Lifecycle? = null
    private var mLayoutToAttachAds: ViewGroup? = null
    private var mAdSize: AdSize? = null
    private var isAdsClose = false


    private val otherShowingObserver: androidx.lifecycle.Observer<String?> =
        object : androidx.lifecycle.Observer<String?> {
            override fun onChanged(value: String?) {
                if (value != null) {
                    // collapsible khác đã được close
                    // show qc
                    try {
                        if (adView != null && mLayoutToAttachAds != null && stateLoadAd == StateLoadAd.SUCCESS) {
                            val viewGroup: ViewGroup? = adView?.parent as ViewGroup?
                            viewGroup?.removeView(adView)
                            mLayoutToAttachAds!!.removeAllViews()
                            mLayoutToAttachAds!!.addView(adView)
                            mLifecycle?.addObserver(lifecycleObserver)
                            mAdCallback?.onAdShow()
                        } else {
                            mAdCallback?.onAdFailToLoad("layout null")
                        }
                    } catch (ex: Exception) {
                        mAdCallback?.onAdFailToLoad(ex.toString())
                    }
                    AdsController.collapsibleShowing.removeObserver(this)
                }
            }
        }

    private val lifecycleObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    adView?.destroy()
                    mLayoutToAttachAds?.removeAllViews()
                    mLifecycle?.removeObserver(this)
                }

                Lifecycle.Event.ON_PAUSE -> {
                    adView?.destroy()
                }

                Lifecycle.Event.ON_RESUME -> {
                    adView?.resume()
                }

                else -> {}
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
        mAdCallback = adCallback
        mAdSize = viewGroupAds?.let { getAdsize(activity, it) }

        if (stateLoadAd != StateLoadAd.LOADING) {
            load(
                activity = activity,
                adsChild = adsChild,
                isPreload = false,
                positionCollapsibleBanner = positionCollapsibleBanner,
                isOneTimeCollapsible = isOneTimeCollapsible,
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
            positionCollapsibleBanner = positionCollapsibleBanner,
            isOneTimeCollapsible = isOneTimeCollapsible,
            isPreload = true
        )
    }

    private fun load(
        activity: Activity,
        adsChild: AdsChild,
        positionCollapsibleBanner: String?,
        isOneTimeCollapsible: Boolean?,
        isPreload: Boolean,
        loadCallback: PreloadCallback? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            stateLoadAd = StateLoadAd.LOADING
            Log.d("TESTERADSEVENT", "start load banner collapsible : ads name ${adsChild.spaceName} id ${adsChild.adsId}")
            val idAds = if (AdsConstant.isDebug) AdsConstant.ID_ADMOB_BANNER_COLLAPSIBLE_TEST else adsChild.adsId

            if (mAdSize == null) {
                mAdSize = getAdsize(activity)
            }

            adView = AdView(activity)
            adView?.setBackgroundColor(Color.WHITE)
            adView?.adUnitId = idAds
            adView?.setAdSize(mAdSize!!)



            adView?.adListener = object : AdListener() {

                override fun onAdOpened() {
                    super.onAdOpened()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d("TESTERADSEVENT", "click banner collapsible : ads name ${adsChild.spaceName} id ${adsChild.adsId}")
                    mAdCallback?.onAdClick()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    mAdCallback?.onAdClose()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    stateLoadAd = StateLoadAd.LOAD_FAILED
                    Log.d("TESTERADSEVENT", "load failed banner collapsible : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ${error.message}")
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
                    Log.d("TESTERADSEVENT", "load success banner collapsible : ads name ${adsChild.spaceName} id ${adsChild.adsId}")
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
                    putString("ad_type", AdDef.ADS_TYPE_ADMOB.BANNER_COLLAPSIBLE)
                    putString("currency_code", adValue.currencyCode)
                }
                mAdCallback?.onPaidEvent(bundle)
            }

            val adsBundle = Bundle().apply {
                if (isOneTimeCollapsible == true) {
                    putString("collapsible_request_id", UUID.randomUUID().toString());
                }
                putString("collapsible", positionCollapsibleBanner ?: "bottom")
            }

            val request = AdRequest
                .Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, adsBundle)
                .build()

            withContext(Dispatchers.Main) {
                adView?.loadAd(request)
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
        mLifecycle = lifecycle
        mAdCallback = adCallback
        mLifecycle?.removeObserver(lifecycleObserver)
        mLayoutToAttachAds = viewGroupAds

        val adSize = getAdsize(activity)
        viewGroupAds?.let { viewG ->
            val lp = viewG.layoutParams
            lp.width = adSize.getWidthInPixels(viewG.context)
            lp.height = adSize.getHeightInPixels(viewG.context)
            viewG.layoutParams = lp
        }


        if (AdsController.collapsibleShowing.value != null) {
            AdsController.collapsibleShowing.observeForever(otherShowingObserver)
            Log.d("TESTERADSEVENT", "show failed banner collapsible : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : other banner collapsible showing")
        } else {
            //neu thang khac khong show thi show luon
            try {
                if (adView != null && viewGroupAds != null) {
                    if (!wasLoadTimeLessThanNHoursAgo()) {
                        stateLoadAd = StateLoadAd.SHOW_FAILED
                        Log.d("TESTERADSEVENT", "show failed banner collapsible : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ads expired")
                        adCallback?.onAdFailToLoad("ads expired")
                    } else {
                        val viewGroup: ViewGroup? = adView?.parent as ViewGroup?
                        viewGroup?.removeView(adView)
                        viewGroupAds.removeAllViews()
                        viewGroupAds.visibility = View.VISIBLE
                        viewGroupAds.addView(adView)
                        mLifecycle?.addObserver(lifecycleObserver)
                        stateLoadAd = StateLoadAd.HAS_BEEN_OPENED
                        Log.d("TESTERADSEVENT", "show success banner collapsible : ads name ${adsChild.spaceName} id ${adsChild.adsId}")
                        mAdCallback?.onAdShow()
                    }
                } else {
                    Log.d("TESTERADSEVENT", "show failed banner collapsible : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : layout null")
                    mAdCallback?.onAdFailToLoad("layout null")
                }
            } catch (ex: Exception) {
                mAdCallback?.onAdFailToLoad(ex.toString())
            }
        }
    }

    override fun setPreloadCallback(preloadCallback: PreloadCallback?) {
        mPreloadCallback = preloadCallback
    }

    override fun removePreloadCallback() {
        mPreloadCallback = null
    }

    private fun getAdsize(activity: Activity): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    private fun getAdsize(mActivity: Activity, adContainer: ViewGroup): AdSize {
        val display = mActivity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)


        val density = outMetrics.density
        var adWidthPixels = adContainer.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth)
    }

    override fun destroyAds() {
        adView = null
        mAdCallback = null
        mPreloadCallback = null
        mLifecycle = null
        mLayoutToAttachAds = null
        mAdSize = null
        isAdsClose = false
        stateLoadAd = StateLoadAd.NULL
    }
}