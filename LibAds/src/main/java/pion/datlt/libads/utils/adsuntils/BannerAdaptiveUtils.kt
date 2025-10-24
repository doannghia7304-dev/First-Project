package pion.datlt.libads.utils.adsuntils

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import pion.datlt.libads.AdsActivity
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.StateLoadAd

fun Fragment.showAdsBannerAdaptive(
    configName: String,
    listSpaceName: List<String>,
    includeHasBeenOpened: Boolean = false,
    viewGroupAds: ViewGroup,
    widthBannerAdaptiveAds: Int? = null
) {
    if (checkConditionShowAds(context, configName)) {
        //chinh lai ratio
        kotlin.runCatching {
            val viewGroupAdsParams = viewGroupAds.layoutParams as ConstraintLayout.LayoutParams?
            viewGroupAdsParams?.width = ConstraintLayout.LayoutParams.MATCH_PARENT
            viewGroupAdsParams?.dimensionRatio = "360:65"
            viewGroupAds.layoutParams = viewGroupAdsParams
        }

        val mapResult = LinkedHashMap<String, StateLoadAd>()
        var isPriorityTimeOut = false

        fun showAds(spaceName: String) {
            mapResult[spaceName] = StateLoadAd.HAS_BEEN_OPENED
            AdsController.getInstance().showLoadedAds(
                spaceName = spaceName,
                includeHasBeenOpened = includeHasBeenOpened,
                viewGroupAds = viewGroupAds,
                widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                adCallback = object : AdCallback {
                    override fun onAdShow() {
                        if (checkIsPreloadAfterShow(spaceNameConfig = configName)) {
                            safePreloadAds(
                                configName = configName,
                                spaceName = spaceName,
                                includeHasBeenOpened = includeHasBeenOpened,
                                widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                            )
                        }
                        context?.let { setTagAdsBanner(it, viewGroupAds) }
                    }

                    override fun onAdClose() {
                        //do nothing
                    }

                    override fun onAdFailToLoad(messageError: String?) {
                        if (checkIsPreloadAfterShow(spaceNameConfig = configName)) {
                            safePreloadAds(
                                configName = configName,
                                spaceName = spaceName,
                                includeHasBeenOpened = includeHasBeenOpened,
                                widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                            )
                        }
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

        val timeOutPriorityRunnable = Runnable {
            isPriorityTimeOut = true
            if (mapResult.any { it.value == StateLoadAd.HAS_BEEN_OPENED }) return@Runnable
            showAnySuccess()
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(timeOutPriorityRunnable, AdsConstant.timeDelayNative)

        fun checkShowAds(spaceName: String) {
            if (mapResult.any { it.value == StateLoadAd.HAS_BEEN_OPENED }) return
            val isAllFailed = mapResult.values.all { it == StateLoadAd.LOAD_FAILED }
            val isAllHigherAdsFailed = mapResult.isAllHigherAdsFailed(spaceName)

            if (isAllFailed) {
                //khong show
                handler.removeCallbacks(timeOutPriorityRunnable)
            } else if (!isPriorityTimeOut && isAllHigherAdsFailed && mapResult[spaceName] == StateLoadAd.SUCCESS) {
                //show luon cai hien tai
                handler.removeCallbacks(timeOutPriorityRunnable)
                showAds(spaceName)
            } else if (isPriorityTimeOut) {
                //show bat ky cai nao success
                handler.removeCallbacks(timeOutPriorityRunnable)
                showAnySuccess()
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
                includeHasBeenOpened = includeHasBeenOpened,
                widthBannerAdaptiveAds = widthBannerAdaptiveAds,
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
        viewGroupAds.visibility = View.GONE
    }
}