package pion.datlt.libads.admob

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import pion.datlt.libads.admob.ads.AdmobAds
import pion.datlt.libads.admob.ads.AdmobBanner300x250Ads
import pion.datlt.libads.admob.ads.AdmobBannerAdaptiveAds
import pion.datlt.libads.admob.ads.AdmobBannerCollapsibleAds
import pion.datlt.libads.admob.ads.AdmobBannerLargeAds
import pion.datlt.libads.admob.ads.AdmobInterstitialAds
import pion.datlt.libads.admob.ads.AdmobNativeAds
import pion.datlt.libads.admob.ads.AdmobNativeFullScreenAds
import pion.datlt.libads.admob.ads.AdmobOpenAppAds
import pion.datlt.libads.admob.ads.AdmobRewardInterstitialAds
import pion.datlt.libads.admob.ads.AdmobRewardVideoAds
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.AdsChild
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.CommonUtils
import pion.datlt.libads.utils.StateLoadAd
import java.util.*


class AdmobHolder {


    private var hashMap: HashMap<String, AdmobAds> = HashMap()

    fun loadAndShow(
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
        var ads: AdmobAds? = null
        val key = adsChild.spaceName.lowercase(Locale.getDefault())

        ads = getAdsByType(adsChild)

        if (ads == null) {
            CommonUtils.showToastDebug(
                activity,
                "not support ${adsChild.adsType} ${adsChild.spaceName} check file json"
            )
            adCallback?.onAdFailToLoad("not support adType ${adsChild.adsType} check file json")
        } else {
            hashMap[key] = ads
            ads.loadAndShow(
                activity = activity,
                adsChild = adsChild,
                destinationToShowAds = destinationToShowAds,
                adCallback = adCallback,
                lifecycle = lifecycle,
                timeout = timeout,
                viewGroupAds = viewGroupAds,
                viewAds = viewAds,
                adChoice = adChoice,
                positionCollapsibleBanner = positionCollapsibleBanner,
                isOneTimeCollapsible = isOneTimeCollapsible,
                widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
            )
        }
    }

    fun showLoadedAds(
        activity: Activity,
        adsChild: AdsChild,
        includeHasBeenOpened: Boolean?,
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
        val key = adsChild.spaceName.lowercase(Locale.getDefault())
        val ads = hashMap[key]

        if (ads == null) {
            //quảng cáo chưa từng được load trước đó
            //thực hiện luồng load and show
            loadAndShow(
                activity = activity,
                adsChild = adsChild,
                destinationToShowAds = destinationToShowAds,
                adCallback = adCallback,
                lifecycle = lifecycle,
                timeout = timeout,
                viewGroupAds = viewGroupAds,
                viewAds = viewAds,
                adChoice = adChoice,
                positionCollapsibleBanner = positionCollapsibleBanner,
                isOneTimeCollapsible = isOneTimeCollapsible,
                widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
            )
        } else {
            hashMap[key] = ads
            if (
                ads.getStateLoadAds() == StateLoadAd.SUCCESS || // quảng cáo đã được load thành công
                (includeHasBeenOpened == true && ads.getStateLoadAds() == StateLoadAd.HAS_BEEN_OPENED)// hoặc quảng cáo đã load và mở(đối với native, banner)
            ) {

                //show luôn
                ads.show(
                    activity = activity,
                    adsChild = adsChild,
                    destinationToShowAds = destinationToShowAds,
                    adCallback = adCallback,
                    lifecycle = lifecycle,
                    viewGroupAds = viewGroupAds,
                    viewAds = viewAds,
                    timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                )
            } else if (ads.getStateLoadAds() == StateLoadAd.LOADING) {

                //set time out o day
                var isTimeout = false
                val handler = Handler(Looper.getMainLooper())
                val timeOutCallback = Runnable {
                    isTimeout = true
                    ads.removePreloadCallback()
                    adCallback?.onAdFailToLoad("TimeOut")
                }
                handler.postDelayed(timeOutCallback, timeout ?: 7000L)


                //qc vẫn đang load, load xong cái là show lên luôn
                ads.setPreloadCallback(
                    preloadCallback = object : PreloadCallback {
                        override fun onLoadDone() {

                            if (!isTimeout) {
                                handler.removeCallbacks(timeOutCallback)
                            }
                            ads.show(
                                activity = activity,
                                adsChild = adsChild,
                                destinationToShowAds = destinationToShowAds,
                                adCallback = adCallback,
                                lifecycle = lifecycle,
                                viewGroupAds = viewGroupAds,
                                viewAds = viewAds,
                                timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                            )
                        }

                        override fun onLoadFail(error: String) {

                            if (!isTimeout) {
                                handler.removeCallbacks(timeOutCallback)
                            }
                            adCallback?.onAdFailToLoad(error)
                        }
                    }
                )
            } else {
                //qc chưa dược load, load and show như bình thường
                ads.loadAndShow(
                    activity = activity,
                    adsChild = adsChild,
                    destinationToShowAds = destinationToShowAds,
                    adCallback = adCallback,
                    lifecycle = lifecycle,
                    timeout = timeout,
                    viewGroupAds = viewGroupAds,
                    viewAds = viewAds,
                    adChoice = adChoice,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                    timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                )
            }

        }
    }

    fun preload(
        activity: Activity,
        adsChild: AdsChild,
        includeHasBeenOpened: Boolean?,
        positionCollapsibleBanner: String?,
        adChoice: Int?,
        isOneTimeCollapsible: Boolean?,
        preloadCallback: PreloadCallback?,
        widthBannerAdaptiveAds: Int?
    ) {
        var ads: AdmobAds? = null

        val key = adsChild.spaceName.lowercase(Locale.getDefault())

        ads = getAdsByType(adsChild)

        if (ads == null) {
            CommonUtils.showToastDebug(
                activity,
                "not support adType ${adsChild.adsType} check file json 3"
            )
            preloadCallback?.onLoadFail("not support adType ${adsChild.adsType} check file json")
        } else {
            hashMap[key] = ads
            if (ads.getStateLoadAds() == StateLoadAd.SUCCESS) {
                preloadCallback?.onLoadDone()
            } else if (includeHasBeenOpened == true && ads.getStateLoadAds() == StateLoadAd.HAS_BEEN_OPENED) {
                preloadCallback?.onLoadDone()
            } else if (ads.getStateLoadAds() == StateLoadAd.LOADING) {
                ads.setPreloadCallback(preloadCallback)
            } else {
                ads.preload(
                    activity = activity,
                    adsChild = adsChild,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    adChoice = adChoice,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds
                )
                ads.setPreloadCallback(preloadCallback) //510cac
            }
        }
    }

    fun getStatusPreload(adChild: AdsChild): StateLoadAd {
        val key = adChild.spaceName.lowercase(Locale.getDefault())
        hashMap[key]?.let {
            return it.getStateLoadAds()
        }
        return StateLoadAd.NULL
    }

    private fun getAdsByType(adsChild: AdsChild): AdmobAds? {
        return when (adsChild.adsType.lowercase(Locale.getDefault())) {
            AdDef.ADS_TYPE_ADMOB.NATIVE -> {
                AdmobNativeAds()
            }

            AdDef.ADS_TYPE_ADMOB.NATIVE_FULL_SCREEN -> {
                AdmobNativeFullScreenAds()
            }

            AdDef.ADS_TYPE_ADMOB.INTERSTITIAL -> {
                AdmobInterstitialAds()
            }

            AdDef.ADS_TYPE_ADMOB.BANNER -> {
                AdmobBanner300x250Ads()
            }

            AdDef.ADS_TYPE_ADMOB.BANNER_ADAPTIVE -> {
                AdmobBannerAdaptiveAds()
            }

            AdDef.ADS_TYPE_ADMOB.BANNER_LARGE -> {
                AdmobBannerLargeAds()
            }

            AdDef.ADS_TYPE_ADMOB.REWARD_VIDEO -> {
                AdmobRewardVideoAds()
            }

            AdDef.ADS_TYPE_ADMOB.REWARD_INTERSTITIAL -> {
                AdmobRewardInterstitialAds()
            }

            AdDef.ADS_TYPE_ADMOB.OPEN_APP -> {
                AdmobOpenAppAds()
            }

            AdDef.ADS_TYPE_ADMOB.BANNER_COLLAPSIBLE -> {
                AdmobBannerCollapsibleAds()
            }

            else -> {
                null
            }
        }
    }

    fun setPreloadCallback(adsChild: AdsChild, preloadCallback: PreloadCallback) {
        val key = adsChild.spaceName.lowercase(Locale.getDefault())
        hashMap[key]?.setPreloadCallback(preloadCallback)
    }

}