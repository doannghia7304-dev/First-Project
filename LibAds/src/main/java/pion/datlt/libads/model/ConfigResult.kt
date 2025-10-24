package pion.datlt.libads.model

import pion.datlt.libads.utils.AdsConstant.NativeInterClosePosition

data class ConfigResult(

    val timeDelayNative: Long = 4000L,
    val positionCloseNativeAfterInter: Int = NativeInterClosePosition.LEFT,
    val disableAllConfig: Boolean = false,
    val isOpenAppOn: Boolean = true,
    val isInterstitialOn: Boolean = true,
    val isNativeOn: Boolean = true,
    val isNativeFullScreenOn: Boolean = true,
    val isBannerOn: Boolean = true,
    val isBannerAdaptiveOn: Boolean = true,
    val isBannerLargeOn: Boolean = true,
    val isBannerInlineOn: Boolean = true,
    val isBannerCollapsibleOn: Boolean = true,
    val isRewardVideoOn: Boolean = true,
    val isRewardInterOn: Boolean = true,
    val isNotificationOn: Boolean = false,
    val notificationTemplate: String? = null,
    val timeShowNotificationAfterLeftApp: Long = 5000L,
    val timeDelayNotification: Long = 15000L,
    val listConfig: List<ConfigAds> = listOf()
)