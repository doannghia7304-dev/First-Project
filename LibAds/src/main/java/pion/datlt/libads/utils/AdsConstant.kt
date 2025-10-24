package pion.datlt.libads.utils

import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.gson.Gson
import pion.datlt.libads.model.ConfigAds
import kotlin.math.truncate

object AdsConstant {

    var listConfigAds: HashMap<String, ConfigAds> = hashMapOf()
    var timeDelayNative = 4000L

    //native after inter close position
    var positionCloseNativeAfterInter = NativeInterClosePosition.LEFT

    //config cho tung dinh dang quang cao
    var disableAllConfig = false
    var isOpenAppOn = true
    var isInterstitialOn = true
    var isNativeOn = true
    var isNativeFullScreenOn = true
    var isBannerOn = true
    var isBannerAdaptiveOn = true
    var isBannerLargeOn = true
    var isBannerInlineOn = true
    var isBannerCollapsibleOn = true
    var isRewardVideoOn = true
    var isRewardInterOn = true

    //config cho notification
    var isNotificationOn = false
    var notificationTemplate : String? = "notification_no_cta1"
    var timeShowNotificationAfterLeftApp = 5000L
    var timeDelayNotification = 15000L

    var isShowToastDebug = false
    var isDebug = false
    var isInternetConnected = true
    var isPremium = false

    //native ad choice
    const val TOP_LEFT = NativeAdOptions.ADCHOICES_TOP_LEFT
    const val TOP_RIGHT = NativeAdOptions.ADCHOICES_TOP_RIGHT
    const val BOTTOM_LEFT = NativeAdOptions.ADCHOICES_BOTTOM_LEFT
    const val BOTTOM_RIGHT = NativeAdOptions.ADCHOICES_BOTTOM_RIGHT

    //native after inter close position
    object NativeInterClosePosition {
        val LEFT = 0
        val CENTER = 1
        val RIGHT = 2
    }

    const val COLLAPSIBLE_BOTTOM = "bottom"
    const val COLLAPSIBLE_TOP = "top"

    //admob id test
    const val ID_ADMOB_APP_ID_TEST = "ca-app-pub-3940256099942544~3347511713"

    const val ID_ADMOB_APP_OPEN_TEST = "ca-app-pub-3940256099942544/9257395921"

    const val ID_ADMOB_INTERSTITIAL_TEST = "ca-app-pub-3940256099942544/1033173712"
    const val ID_ADMOB_INTERSTITIAL_VIDEO_TEST = "ca-app-pub-3940256099942544/8691691433"

    const val ID_ADMOB_REWARD_TEST = "ca-app-pub-3940256099942544/5224354917"
    const val ID_ADMOB_REWARD_INTERSTITIAL_TEST = "ca-app-pub-3940256099942544/5354046379"

    const val ID_ADMOB_NATIVE_TEST = "ca-app-pub-3940256099942544/2247696110"
    const val ID_ADMOB_NATIVE_VIDEO_TEST = "ca-app-pub-3940256099942544/1044960115"

    const val ID_ADMOB_BANNER_TEST = "ca-app-pub-3940256099942544/6300978111"
    const val ID_ADMOB_BANNER_ADAPTIVE_TEST = "ca-app-pub-3940256099942544/9214589741"
    const val ID_ADMOB_BANNER_COLLAPSIBLE_TEST = "ca-app-pub-3940256099942544/2014213617"


    //mintergral id test
    const val ID_MINTEGRAL_APP_KEY_TEST = "7c22942b749fe6a6e361b675e96b3ee9"
    const val ID_MINTEGRAL_APP_ID_TEST = "144002"

    //banner
    const val ID_MINTEGRAL_BANNER_PLACEMENT_TEST = "1010694"
    const val ID_MINTEGRAL_BANNER_TEST = "2677203"

    //native
    const val ID_MINTEGRAL_NATIVE_PLACEMENT_TEST = "290656"
    const val ID_MINTEGRAL_NATIVE_TEST = "1542107"

    //reward video
    const val ID_MINTEGRAL_REWARD_PLACEMENT_TEST = "290651"
    const val ID_MINTEGRAL_REWARD_TEST = "1542101"

    //interstitial
    const val ID_MINTEGRAL_INTERSTITIAL_PLACEMENT_TEST = "290653"
    const val ID_MINTEGRAL_INTERSTITIAL_TEST = "1542103"

    //open app
    const val ID_MINTEGRAL_OPEN_PLACEMENT_TEST = "328916"
    const val ID_MINTEGRAL_OPEN_TEST = "1566319"

    //auto native
    const val ID_MINTEGRAL_AUTO_NATIVE_PLACEMENT_TEST = "328917"
    const val ID_MINTEGRAL_AUTO_NATIVE_TEST = "1592613"


    const val TIME_OUT_DEFAULT = 7000L
    const val DELAY_INTER_DEFAULT = 10
    const val RESHOW_NATIVE_TIME = 10
    const val ERROR_NO_INTERNET = "error_no_internet"
    const val ERROR_OFF_BY_TYPE = "error_off_by_type"
    const val ERROR_PREMIUM = "error_premium"
    const val ERROR_INIT = "ads not init"


    const val TEST_CONFIG_ADS = ""
}