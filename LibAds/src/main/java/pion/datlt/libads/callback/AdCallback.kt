package pion.datlt.libads.callback

import android.os.Bundle
import android.util.Log
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import pion.datlt.libads.AdsController
import kotlin.collections.HashMap

/**
 * callback khi thực hiện load hoặc show quảng cáo.
 */
interface AdCallback {

    /**
     * gọi khi quảng cáo được show lên.
     */
    fun onAdShow()

    /**
     * gọi khi quảng cáo được đóng lại.
     */
    fun onAdClose()

    /**
     * gọi khi không thể show quảng cáo hoặc quá trình load có vấn đề hoặc đã quá thời gian để load quảng cáo.
     */
    fun onAdFailToLoad(messageError: String?)

    /**
     * gọi khi tắt quảng cáo.
     */
    fun onAdOff(){

    }

    /**
     * gọi khi click vào quảng cáo.
     */
    fun onAdClick() {
        AdsController.isBlockOpenAds = true
    }

    /**
     * gọi khi đóng phần mở rộng của quảng cáo native collapsible.
     */
    fun onClickCloseNativeCollapsible() {

    }

    /**
     * gọi khi đã nhận 1 lượt impression.
     * nhớ gọi hàm supper vì nó dùng để bắn appflyer revenue
     */
    fun onPaidEvent(params: Bundle) {

        val adUnitID = params.getString("ad_unit_id") ?: ""
        val adSourceId = params.getString("ad_source_id")
        val adSourceName = params.getString("ad_source_name") ?: ""
        val precisionType = params.getInt("precision_type")
        val revenue: Double = (params.getLong("revenue_micros") ?: 0).toDouble() / 1000000L
        val adType = params.getString("ad_type") ?: ""
        val currencyCode = params.getString("currency_code") ?: ""


        val appsflyer = AppsFlyerLib.getInstance()

        val adRevenueData = AFAdRevenueData(
            "admob",
            MediationNetwork.GOOGLE_ADMOB,
            currencyCode,
            revenue
        )


        val additionalParameters: MutableMap<String, Any> = HashMap()

        additionalParameters[AdRevenueScheme.AD_UNIT] = adUnitID
        additionalParameters[AdRevenueScheme.AD_TYPE] = adType
        additionalParameters["ad_source_Name"] = adSourceName
        additionalParameters["precisionType"] = precisionType.toString()


        appsflyer.logAdRevenue(adRevenueData, additionalParameters)

        Log.d(
            "CHECKONPAIDEVENT", "onPaidEvent: adUnitID = $adUnitID " +
                    "adSourceId = $adSourceId " +
                    "adSourceName = $adSourceName " +
                    "precisionType = $precisionType " +
                    "revenueMicros = $revenue" +
                    "adType = $adType " +
                    "currencyCode = $currencyCode"
        )

    }

    /**
     * gọi khi đã nhận reward thành công(dùng cho quảng cáo reward).
     */
    fun onGotReward(){

    }



}