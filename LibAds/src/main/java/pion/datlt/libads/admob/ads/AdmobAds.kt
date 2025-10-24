package pion.datlt.libads.admob.ads

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.AdsChild
import pion.datlt.libads.utils.StateLoadAd
import java.util.*

/**
 * Class thực hiện các hành động load và show quảng cáo Admob.
 * Tất cả các quảng cáo được load, hay preload sẽ được lưu trữ ở đây
 */
abstract class AdmobAds {

    protected var timeLoader = 0L //thoi gian load success
    protected var stateLoadAd: StateLoadAd = StateLoadAd.NONE

    protected var adSourceId = ""
    protected var adSourceName = ""
    protected var adUnitId = ""


    abstract fun loadAndShow(
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
    )

    abstract fun preload(
        activity: Activity,
        adsChild: AdsChild,
        positionCollapsibleBanner: String?,
        adChoice: Int?,
        isOneTimeCollapsible: Boolean?,
        widthBannerAdaptiveAds: Int?
    )

    abstract fun show(
        activity: Activity,
        adsChild: AdsChild,
        destinationToShowAds: Int?,
        adCallback: AdCallback?,
        lifecycle: Lifecycle?,
        //native
        viewGroupAds: ViewGroup?,
        viewAds: View?,
        timeShowNativeCollapsibleAfterClose: Int?
    )

    abstract fun setPreloadCallback(preloadCallback: PreloadCallback?)

    abstract fun removePreloadCallback()

    abstract fun destroyAds()

    fun getStateLoadAds() = stateLoadAd


    fun wasLoadTimeLessThanNHoursAgo(numHours: Long = 1): Boolean {
        val dateDifference: Long = Date().time - timeLoader
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

}