package pion.datlt.libads

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.google.android.gms.ads.AdInspectorError
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.UserMessagingPlatform
import com.google.gson.Gson
import pion.datlt.libads.admob.AdmobHolder
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.Ads
import pion.datlt.libads.model.AdsChild
import pion.datlt.libads.model.ConfigResult
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.CommonUtils
import pion.datlt.libads.utils.ConnectUtils
import pion.datlt.libads.utils.DialogNative
import pion.datlt.libads.utils.NativeInterListener
import pion.datlt.libads.utils.StateLoadAd
import pion.datlt.libads.utils.adsuntils.checkAdsByType
import pion.datlt.libads.utils.adsuntils.safePreloadAds
import pion.datlt.libads.utils.adsuntils.setLastTimeShowInter
import java.io.DataInput
import java.util.*
import kotlin.collections.ArrayList

/**
 * Object quản lý các hành động liên quan đến load và show quảng cáo.
 *
 * @param activity: activity đang hiển thị trên màn hình.
 * @param packageName: packageName của ứng dụng.
 * @param listAppId: danh sách các app id của ứng dung(dùng trong trường hợp gắn nhiều mạng quảng cáo).
 * @param listPathJson: danh sách các file json chứa id của quảng cáo(dùng trong trường hợp gắn nhiều mạng quảng cáo).
 * @param lifecycleActivity: lifecycle của activity đang hiển thị.
 */
class AdsController private constructor(
    var activity: Activity,
    var listAppId: ArrayList<String>,
    var packageName: String
) {

    private val gson = Gson()

    @SuppressLint("RestrictedApi")
    private val destinationChangeListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            currentDestinationId = destination.id
            Log.d("TESTERADSEVENT", "")
            Log.d("TESTERADSEVENT", "")
            Log.d("TESTERADSEVENT", "")
            Log.d("TESTERADSEVENT", "")
            Log.d("TESTERADSEVENT", "")
            Log.d("TESTERADSEVENT", "screen change to: ${destination.displayName}")
            Log.d("TESTERADSEVENT", "")
            Log.d("TESTERADSEVENT", "")
            Log.d("TESTERADSEVENT", "")
            Log.d("TESTERADSEVENT", "")
            Log.d("TESTERADSEVENT", "")
        }


    /**
     * hashmap tất cả các quảng cáo trong các file json
     *
     * key : spaceName của quảng cáo
     *
     * value : ArrayList các quảng cáo có cùng ID
     */
    private val hashMapAds: HashMap<String, ArrayList<AdsChild>> = hashMapOf()
    private var connectionLiveData: ConnectUtils = ConnectUtils(activity)


    private val admobHolder = AdmobHolder()

    //gdpr
    val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

    var listJsonData: ArrayList<String> = arrayListOf()


    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var adsController: AdsController

        var lastTimeShowAdsInter = 0L

        /**
         * giá trị = true khi muốn block app resume ở lần trở lại app tiếp theo
         */
        var isBlockOpenAds = true

        /**
         * giá trị = true khi một app resume khác đang show lên
         */
        var isOtherOpenAdsIsShowing = false

        /**
         * giá trị = true khi một interstitial khác đang show lên
         */
        var isInterIsShowing = false

        /**
         * id của fragment đang show hiện tại. Dùng để check xem quảng cáo có show đúng màn hay không
         */
        var currentDestinationId = -1

        /**
         * lìve data space name của quảng cáo collapsible đang được show. Dùng để tắt collapsible khi quảng cáo collapsible khác đang show lên
         */
        val collapsibleShowing = MutableLiveData<String?>(null)


        fun init(
            activity: Activity,
            isDebug: Boolean,
            listAppId: ArrayList<String>,
            packageName: String,
            navController: NavController
        ) {
            Log.d("CHECTHREAD", "init: call from function 1")

            AdsConstant.isDebug = isDebug
            AdsConstant.isShowToastDebug = isDebug

            initAppFlyer(activity)

            Log.d("CHECTHREAD", "init: call from function 2")

            if (checkInit()) {
                adsController.activity = activity
                adsController.listAppId = listAppId
                adsController.packageName = packageName
            } else {
                adsController = AdsController(
                    activity = activity,
                    listAppId = listAppId,
                    packageName = packageName
                )
            }
            Log.d("CHECTHREAD", "init: call from function 3")


            navController.apply {
                currentDestinationId = currentDestination?.id ?: -1
                runCatching { removeOnDestinationChangedListener(adsController.destinationChangeListener) }
                addOnDestinationChangedListener(adsController.destinationChangeListener)
            }

        }

        private fun initAppFlyer(activity: Activity) {
            try {
                AppsFlyerLib.getInstance().init("4Ti9yuyaVb6BJMoy25gWUP", null, activity)
                AppsFlyerLib.getInstance().start(activity, "4Ti9yuyaVb6BJMoy25gWUP", object :
                    AppsFlyerRequestListener {
                    override fun onSuccess() {

                    }

                    override fun onError(errorCode: Int, errorDesc: String) {

                    }
                })

                AppsFlyerLib.getInstance().setDebugLog(AdsConstant.isDebug)

            } catch (e: Exception) {

            }
        }

        fun getInstance(): AdsController {
            if (!::adsController.isInitialized) {
                throw Throwable("you must call init before get instance")
            }
            return adsController
        }

        fun checkInit() = ::adsController.isInitialized


        fun setConfigAds(dataJson: String) {
            if (dataJson.isNotEmpty()) {
                val gson = Gson()
                val objectResult = gson.fromJson(dataJson, ConfigResult::class.java)


                //remote cho tung vi tri
                AdsConstant.apply {
                    timeDelayNative = objectResult.timeDelayNative
                    disableAllConfig = objectResult.disableAllConfig
                    isOpenAppOn = objectResult.isOpenAppOn
                    isInterstitialOn = objectResult.isInterstitialOn
                    isNativeOn = objectResult.isNativeOn
                    isNativeFullScreenOn = objectResult.isNativeFullScreenOn
                    isBannerOn = objectResult.isBannerOn
                    isBannerAdaptiveOn = objectResult.isBannerAdaptiveOn
                    isBannerLargeOn = objectResult.isBannerLargeOn
                    isBannerInlineOn = objectResult.isBannerInlineOn
                    isBannerCollapsibleOn = objectResult.isBannerCollapsibleOn
                    isRewardVideoOn = objectResult.isRewardVideoOn
                    isRewardInterOn = objectResult.isRewardInter
                }


                for (config in objectResult.listConfig) {
                    AdsConstant.listConfigAds[config.nameConfig] = config
                    Log.d("CHECKADSCONFIG", "setConfigAds: ${config.nameConfig} ${config.isOn}")
                }
            }
        }
    }

    init {
        Log.d("CHECTHREAD", "init: by default")

        //theo doi ket noi mang
        connectionLiveData.observe(activity as LifecycleOwner) {
            AdsConstant.isInternetConnected = it
        }
    }

    fun setListAdsData(listJsonData: ArrayList<String>) {
        if (!checkInit()) return

        this.listJsonData = listJsonData

        //lay ads tu json
        val listAds = ArrayList<Ads>()
        for (item in listJsonData) {
            try {
                val ads = gson.fromJson(item, Ads::class.java)
                listAds.add(ads)
                initAdsByMediation(ads)
            } catch (e: Exception) {
                CommonUtils.showToastDebug(activity, "no load data json ads file")
            }
        }

        for (ads in listAds) {
            for (adsChild in ads.listAdsChild) {
                if (!checkAppIdAndPacketName(ads)) continue
                adsChild.network = ads.network
                adsChild.adsId = adsChild.adsId.trim()//bo dau cach
                if (adsChild.priority == -1) adsChild.priority = ads.priority
                var listItem = hashMapAds[adsChild.spaceName.lowercase()]
                if (listItem == null) {
                    listItem = java.util.ArrayList()
                    hashMapAds[adsChild.spaceName.lowercase()] = listItem
                }
                listItem.add(adsChild)
            }
        }
    }

    private fun initAdsByMediation(ads: Ads) {
        when (ads.network) {
            AdDef.NETWORK.GOOGLE -> {
                MobileAds.initialize(activity) {

                }
            }

            AdDef.NETWORK.MINTEGRAL -> {
                //init mintergral
            }

            AdDef.NETWORK.PANGLE -> {
                //init pangle
            }

            else -> {
                //do nothing
            }
        }
    }

    private fun checkAppIdAndPacketName(ads: Ads): Boolean {
        var checkAppId = false
        var checkPacket = false
        //check xem id nay co ton tai trong list app id khong
        checkAppId = listAppId.any { ads.appId == it }
        if (!checkAppId) CommonUtils.showToastDebug(activity, "wrong appId network ${ads.network}")

        //check xem package name trong file json da dung voi package name cua app hay chua
        if (ads.packageName != packageName) {
            CommonUtils.showToastDebug(activity, "wrong packetName")
        } else {
            checkPacket = true
        }
        return checkAppId && checkPacket
    }

    fun openInspector(onClose: (err: AdInspectorError?) -> Unit) {
        MobileAds.openAdInspector(
            activity,
        ) { p0 -> onClose.invoke(p0) }
    }

    /**
     * hàm lấy ra adsChild có độ ưu tiên cao nhất
     *
     * @param listItem: list các quảng cáo có cùng spaceName
     *
     * @return AdsChild có mức độ ưu tiên cao nhất
     */
    private fun getHighestChildPriority(listItem: ArrayList<AdsChild>): AdsChild? {
        var value = Int.MIN_VALUE
        var adsChild: AdsChild? = null
        for (item in listItem) {
            if (item.priority > value) {
                value = item.priority
                adsChild = item
            }
        }
        return adsChild
    }


    fun loadAndShow(
        spaceName: String,
        destinationToShowAds: Int? = null,
        lifecycle: Lifecycle? = null,
        timeout: Long? = null,
        layoutToAttachAds: ViewGroup? = null,
        viewAdsInflateFromXml: View? = null,
        adChoice: Int? = null,
        positionCollapsibleBanner: String? = null,
        isOneTimeCollapsible: Boolean? = null,
        adCallback: AdCallback? = null,
        widthBannerAdaptiveAds: Int? = null,
        timeShowNativeCollapsibleAfterClose: Int? = null
    ) {
        if (AdsConstant.isPremium) {
            layoutToAttachAds?.visibility = View.GONE
            adCallback?.onAdFailToLoad(AdsConstant.ERROR_PREMIUM)
            return
        }
        if (!AdsConstant.isInternetConnected) {
            adCallback?.onAdFailToLoad(AdsConstant.ERROR_NO_INTERNET)
            return
        }
        if (!checkAdsByType(spaceName)) {
            adCallback?.onAdFailToLoad(AdsConstant.ERROR_OFF_BY_TYPE)
            return
        }


        val contextUse = this.activity
        val listItem = hashMapAds[spaceName.lowercase()]
        if (listItem == null || listItem.size == 0) {
            CommonUtils.showToastDebug(contextUse, "no data check spaceName or file json 1")
            adCallback?.onAdFailToLoad("no data check spaceName or file json")
        } else {
            val adsChild = getHighestChildPriority(listItem)
            if (adsChild != null) {
                loadAndShowAdsByMediation(
                    adsChild = adsChild,
                    destinationToShowAds = destinationToShowAds,
                    adCallback = adCallback,
                    lifecycle = lifecycle,
                    timeout = timeout,
                    layoutToAttachAds = layoutToAttachAds,
                    viewAdsInflateFromXml = viewAdsInflateFromXml,
                    adChoice = adChoice,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                    timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                )
            } else {
                CommonUtils.showToastDebug(contextUse, "no data check priority file json")
                adCallback?.onAdFailToLoad("")
            }
        }
    }

    private fun loadAndShowAdsByMediation(
        adsChild: AdsChild,
        destinationToShowAds: Int?,
        adCallback: AdCallback?,
        lifecycle: Lifecycle?,
        timeout: Long?,
        layoutToAttachAds: ViewGroup?,
        viewAdsInflateFromXml: View?,
        adChoice: Int?,
        positionCollapsibleBanner: String?,
        isOneTimeCollapsible: Boolean?,
        widthBannerAdaptiveAds: Int?,
        timeShowNativeCollapsibleAfterClose: Int?
    ) {
        when (adsChild.network.lowercase(Locale.getDefault())) {
            AdDef.NETWORK.GOOGLE -> {
                admobHolder.loadAndShow(
                    activity = activity,
                    adsChild = adsChild,
                    destinationToShowAds = destinationToShowAds,
                    adCallback = adCallback,
                    lifecycle = lifecycle,
                    timeout = timeout,
                    layoutToAttachAds = layoutToAttachAds,
                    viewAdsInflateFromXml = viewAdsInflateFromXml,
                    adChoice = adChoice,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                    timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                )
            }

            AdDef.NETWORK.MINTEGRAL -> {

            }

            AdDef.NETWORK.PANGLE -> {

            }

            else -> {

            }
        }

    }

    fun showLoadedAds(
        spaceName: String,
        includeHasBeenOpened: Boolean? = null,
        destinationToShowAds: Int? = null,
        lifecycle: Lifecycle? = null,
        timeout: Long? = null,
        layoutToAttachAds: ViewGroup? = null,
        viewAdsInflateFromXml: View? = null,
        adChoice: Int? = null,
        positionCollapsibleBanner: String? = null,
        isOneTimeCollapsible: Boolean? = null,
        adCallback: AdCallback? = null,
        widthBannerAdaptiveAds: Int? = null,
        timeShowNativeCollapsibleAfterClose: Int? = null
    ) {
        if (AdsConstant.isPremium) {
            layoutToAttachAds?.visibility = View.GONE
            adCallback?.onAdFailToLoad(AdsConstant.ERROR_PREMIUM)
            return
        }
        if (!AdsConstant.isInternetConnected) {
            adCallback?.onAdFailToLoad(AdsConstant.ERROR_NO_INTERNET)
            return
        }
        if (!checkAdsByType(spaceName)) {
            adCallback?.onAdFailToLoad(AdsConstant.ERROR_OFF_BY_TYPE)
            return
        }

        val listAdsChild = hashMapAds[spaceName.lowercase()]
        if (listAdsChild.isNullOrEmpty()) {
            CommonUtils.showToastDebug(activity, "no data check spaceName or file json")
            adCallback?.onAdFailToLoad("no data check spaceName or file json")
        } else {
            val adsChild = getHighestChildPriority(listAdsChild)
            if (adsChild != null) {
                showLoadedAdsByMediation(
                    adsChild = adsChild,
                    includeHasBeenOpened = includeHasBeenOpened,
                    destinationToShowAds = destinationToShowAds,
                    layoutToAttachAds = layoutToAttachAds,
                    viewAdsInflateFromXml = viewAdsInflateFromXml,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    adChoice = adChoice,
                    lifecycle = lifecycle,
                    timeout = timeout,
                    adCallback = adCallback,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                    timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                )
            } else {
                CommonUtils.showToastDebug(activity, "no data check spaceName or file json")
                adCallback?.onAdFailToLoad("no data check spaceName or file json")
            }
        }
    }

    private fun showLoadedAdsByMediation(
        adsChild: AdsChild,
        includeHasBeenOpened: Boolean?,
        destinationToShowAds: Int?,
        adCallback: AdCallback?,
        lifecycle: Lifecycle?,
        timeout: Long?,
        layoutToAttachAds: ViewGroup?,
        viewAdsInflateFromXml: View?,
        adChoice: Int?,
        positionCollapsibleBanner: String?,
        isOneTimeCollapsible: Boolean?,
        widthBannerAdaptiveAds: Int?,
        timeShowNativeCollapsibleAfterClose: Int?
    ) {
        when (adsChild.network.lowercase()) {
            AdDef.NETWORK.GOOGLE -> {
                admobHolder.showLoadedAds(
                    activity = activity,
                    adsChild = adsChild,
                    includeHasBeenOpened = includeHasBeenOpened,
                    destinationToShowAds = destinationToShowAds,
                    adCallback = adCallback,
                    lifecycle = lifecycle,
                    timeout = timeout,
                    layoutToAttachAds = layoutToAttachAds,
                    viewAdsInflateFromXml = viewAdsInflateFromXml,
                    adChoice = adChoice,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                    timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                )
            }

            AdDef.NETWORK.PANGLE -> {

            }

            AdDef.NETWORK.MINTEGRAL -> {

            }

            else -> {

            }
        }
    }

    fun preload(
        spaceName: String,
        includeHasBeenOpened: Boolean? = null,
        positionCollapsibleBanner: String? = null,
        adChoice: Int? = null,
        isOneTimeCollapsible: Boolean? = null,
        preloadCallback: PreloadCallback? = null,
        widthBannerAdaptiveAds: Int? = null
    ) {
        if (AdsConstant.isPremium) {
            preloadCallback?.onLoadFail(AdsConstant.ERROR_PREMIUM)
            return
        }
        if (!AdsConstant.isInternetConnected) {
            preloadCallback?.onLoadFail(AdsConstant.ERROR_NO_INTERNET)
            return
        }
        if (!checkAdsByType(spaceName)) {
            preloadCallback?.onLoadFail(AdsConstant.ERROR_OFF_BY_TYPE)
            return
        }

        val listItem = hashMapAds[spaceName.lowercase()]
        if (listItem.isNullOrEmpty()) {
            CommonUtils.showToastDebug(activity, "no data check spaceName or file json")
            preloadCallback?.onLoadFail("no data check spaceName or file json")
        } else {
            val adsChild = getHighestChildPriority(listItem)
            if (adsChild != null) {
                preloadAdsByMediation(
                    activity = activity,
                    adsChild = adsChild,
                    includeHasBeenOpened = includeHasBeenOpened,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    adChoice = adChoice,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    preloadCallback = preloadCallback,
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds
                )
            } else {
                CommonUtils.showToastDebug(activity, "no data check priority file json")
                preloadCallback?.onLoadFail("")
            }
        }
    }

    private fun preloadAdsByMediation(
        activity: Activity,
        adsChild: AdsChild,
        includeHasBeenOpened: Boolean?,
        positionCollapsibleBanner: String?,
        adChoice: Int?,
        isOneTimeCollapsible: Boolean?,
        preloadCallback: PreloadCallback?,
        widthBannerAdaptiveAds: Int?
    ) {
        when (adsChild.network.lowercase()) {
            AdDef.NETWORK.GOOGLE -> {
                admobHolder.preload(
                    activity = activity,
                    adsChild = adsChild,
                    includeHasBeenOpened = includeHasBeenOpened,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    adChoice = adChoice,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    preloadCallback = preloadCallback,
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds
                )
            }

            AdDef.NETWORK.PANGLE -> {

            }

            AdDef.NETWORK.MINTEGRAL -> {

            }

            else -> {

            }
        }
    }

    fun setPreloadCallback(spaceName: String, preloadCallback: PreloadCallback?) {
        if (AdsConstant.isPremium || !AdsConstant.isInternetConnected) {
            return
        }
        preloadCallback ?: return


        hashMapAds[spaceName.lowercase(Locale.getDefault())]?.let { listAdChild ->
            if (listAdChild.isNotEmpty()) {
                getHighestChildPriority(listAdChild)?.let { adChild ->
                    when (adChild.network.lowercase(Locale.getDefault())) {
                        AdDef.NETWORK.GOOGLE -> {
                            admobHolder.setPreloadCallback(adChild, preloadCallback)
                        }

                        AdDef.NETWORK.PANGLE -> {
                            //do nothing
                        }

                        else -> {
                            //do nothing
                        }
                    }
                }
            }
        }


    }

    fun checkAdsState(spaceName: String): StateLoadAd {
        if (AdsConstant.isPremium) {
            return StateLoadAd.NONE
        }

        hashMapAds[spaceName.lowercase()]?.let { listAdChild ->
            if (listAdChild.isNotEmpty()) {
                getHighestChildPriority(listAdChild)?.let { adChild ->
                    when (adChild.network.lowercase(Locale.getDefault())) {
                        AdDef.NETWORK.GOOGLE -> {
                            return admobHolder.getStatusPreload(adChild)
                        }

                        AdDef.NETWORK.PANGLE -> {
                            return StateLoadAd.NONE
                        }

                        AdDef.NETWORK.MINTEGRAL -> {
                            return StateLoadAd.NONE
                        }

                        else -> {
                            return StateLoadAd.NONE
                        }
                    }
                }
            }
        }


        return StateLoadAd.NONE
    }

    fun getAdsDetail(spaceName: String): AdsChild? {
        if (AdsConstant.isPremium) return null

        hashMapAds[spaceName.lowercase()]?.let { listAdChild ->
            if (listAdChild.isNotEmpty()) {
                getHighestChildPriority(listAdChild)?.let { adChild ->
                    return adChild
                }
            }
        }
        return null
    }

    fun closeCollapsibleBanner(
        spaceName: String,
        lifecycleOwner: LifecycleOwner,
        onDone: () -> Unit
    ) {
        hashMapAds[spaceName.lowercase(Locale.getDefault())]?.let { listAdChild ->
            if (listAdChild.isNotEmpty()) {
                getHighestChildPriority(listAdChild)?.let { adChild ->
                    when (adChild.network.lowercase(Locale.getDefault())) {
                        AdDef.NETWORK.GOOGLE -> {
                            if (adChild.adsType == AdDef.ADS_TYPE_ADMOB.BANNER_COLLAPSIBLE) {
                                admobHolder.closeCollapsibleBanner(adChild, lifecycleOwner, onDone)
                            } else {
                                onDone.invoke()
                            }
                        }

                        else -> {
                            onDone.invoke()
                        }
                    }
                }
            }
        } ?: kotlin.run {
            onDone.invoke()
        }
    }


    fun initResumeAds(
        lifecycle: Lifecycle,
        listSpaceName: List<String>,
        onShowOpenApp: () -> Unit,
        onStartToShowOpenAds: () -> Unit,
        onCloseOpenApp: () -> Unit,
        onPaidEvent: (params: Bundle) -> Unit
    ) {
        isBlockOpenAds = true
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    var isAnyAdShowed = false
                    listSpaceName.forEach { currentResumeAppSpaceName ->
                        Log.d(
                            "CHECKRESUMEAPP",
                            "initResumeAds: ${checkAdsState(currentResumeAppSpaceName)} ${!isBlockOpenAds} ${!isInterIsShowing} ${!isOtherOpenAdsIsShowing}"
                        )
                        if (checkAdsState(currentResumeAppSpaceName) == StateLoadAd.SUCCESS) {
                            if (!isBlockOpenAds && !isInterIsShowing && !isOtherOpenAdsIsShowing && !DialogNative.isShowing()) {
                                isOtherOpenAdsIsShowing = true
                                isAnyAdShowed = true
                                //show
                                DialogNative.dismiss()
                                onStartToShowOpenAds.invoke()
                                showLoadedAds(
                                    spaceName = currentResumeAppSpaceName,
                                    lifecycle = lifecycle,
                                    adCallback = object : AdCallback {
                                        override fun onAdShow() {
                                            onShowOpenApp.invoke()
                                        }

                                        override fun onAdClose() {
                                            onCloseOpenApp.invoke()
                                            lifecycle.addObserver(object : LifecycleEventObserver {
                                                override fun onStateChanged(
                                                    source: LifecycleOwner,
                                                    event: Lifecycle.Event
                                                ) {
                                                    if (event == Lifecycle.Event.ON_RESUME) {
                                                        Handler(Looper.getMainLooper()).postDelayed(
                                                            {
                                                                isOtherOpenAdsIsShowing = false
                                                            },
                                                            2000
                                                        )
                                                        Handler(Looper.getMainLooper()).postDelayed(
                                                            {
                                                                if (checkAdsState(
                                                                        currentResumeAppSpaceName
                                                                    ) != StateLoadAd.LOADING
                                                                ) {
                                                                    preload(
                                                                        currentResumeAppSpaceName
                                                                    )
                                                                }
                                                            },
                                                            500
                                                        )
                                                        lifecycle.removeObserver(this)
                                                    }
                                                }
                                            })
                                        }

                                        override fun onAdFailToLoad(messageError: String?) {
                                            onCloseOpenApp.invoke()

                                            isOtherOpenAdsIsShowing = false
                                            lifecycle.addObserver(object : LifecycleEventObserver {
                                                override fun onStateChanged(
                                                    source: LifecycleOwner,
                                                    event: Lifecycle.Event
                                                ) {
                                                    if (event == Lifecycle.Event.ON_RESUME) {
                                                        Handler(Looper.getMainLooper()).postDelayed(
                                                            {
                                                                if (checkAdsState(
                                                                        currentResumeAppSpaceName
                                                                    ) != StateLoadAd.LOADING
                                                                ) {
                                                                    preload(
                                                                        currentResumeAppSpaceName
                                                                    )
                                                                }
                                                            },
                                                            500
                                                        )
                                                        lifecycle.removeObserver(this)
                                                    }
                                                }
                                            })
                                        }

                                        override fun onAdOff() {

                                        }

                                        override fun onPaidEvent(params: Bundle) {
                                            super.onPaidEvent(params)
                                            onPaidEvent.invoke(params)
                                        }

                                    }
                                )
                            }
                        } else {
                            if (checkAdsState(currentResumeAppSpaceName) != StateLoadAd.LOADING) {
                                preload(
                                    currentResumeAppSpaceName,
                                    preloadCallback = object : PreloadCallback {
                                        override fun onLoadDone() {
                                            Log.d("CHECKRESUMEAPP", "onLoadDone: load done")
                                        }

                                        override fun onLoadFail(error: String) {
                                            Log.d(
                                                "CHECKRESUMEAPP",
                                                "onLoadDone: load failed $error"
                                            )
                                        }

                                    })
                            }
                        }
                    }
                }

                else -> {

                }
            }
        })
    }


    var showNativeTrigger: (() -> Unit)? = null

    fun initNativeInter(
        activity: Activity,
        configName: String,
        listSpaceName: List<String>
    ) {
        fun preloadAds() {
            if (AdsConstant.listConfigAds[configName]?.isOn == true) {
                listSpaceName.forEach { spaceNameAds ->
                    activity.safePreloadAds(
                        spaceNameConfig = configName,
                        spaceNameAds = spaceNameAds,
                        includeHasBeenOpened = false,
                        adChoice = AdsConstant.BOTTOM_LEFT,
                        preloadCallback = object : PreloadCallback {
                            override fun onLoadDone() {
                                //do nothing
                            }

                            override fun onLoadFail(error: String) {
                                super.onLoadFail(error)
                                //do nothing
                            }
                        },
                    )
                }
            }
        }

        preloadAds()

        showNativeTrigger = {
            for (spaceName in listSpaceName) {
                if (checkAdsState(spaceName) == StateLoadAd.SUCCESS) {
                    //show qc dau tien thanh cong
                    if (AdsConstant.listConfigAds[configName]?.isOn == true) {
                        DialogNative.show(
                            activity,
                            configName,
                            spaceName,
                            object : NativeInterListener {
                                override fun onShowNative() {

                                }

                                override fun onCloseNative() {
                                    preloadAds()
                                }

                                override fun onClickNative() {
                                }
                            })
                    }
                    break
                } else {
                    //load lai
                    preloadAds()
                }
            }
        }
    }

}