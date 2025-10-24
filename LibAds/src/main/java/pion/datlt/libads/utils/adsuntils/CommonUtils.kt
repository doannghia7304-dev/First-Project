package pion.datlt.libads.utils.adsuntils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import pion.datlt.libads.AdsController
import pion.datlt.libads.R
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.ConfigAds
import pion.datlt.libads.model.ConfigNative
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.StateLoadAd

fun checkConditionShowAds(context: Context?, configName: String): Boolean {
    context ?: return false
    val config: ConfigAds? = AdsConstant.listConfigAds[configName]
    val isOn = config?.isOn ?: false
    if (AdsConstant.disableAllConfig) return false
    return (AdsConstant.isInternetConnected
            && !AdsConstant.isPremium
            && isOn
            && isOverTimeDelay(configName = configName))
}




private fun isOverTimeDelay(configName: String): Boolean {
    val config: ConfigAds? = AdsConstant.listConfigAds[configName]
    config ?: return false
    val timeDelay = config.timeDelayShowInter
    timeDelay ?: return true
    val isTimeOut =
        System.currentTimeMillis() - AdsController.lastTimeShowAdsInter > timeDelay * 1000
    return isTimeOut
}

fun setLastTimeShowInter() {
    AdsController.lastTimeShowAdsInter = System.currentTimeMillis()
}

fun Fragment.safePreloadAds(
    configName: String,
    spaceName: String,
    //native
    adChoice: Int? = null,
    includeHasBeenOpened: Boolean = false,
    //banner
    positionCollapsibleBanner: String? = null,
    isOneTimeCollapsible: Boolean? = null,
    widthBannerAdaptiveAds: Int? = null,
    preloadCallback: PreloadCallback? = null,
) {
    val config = AdsConstant.listConfigAds[configName]
    val isOn = config?.isOn ?: false
    val isTypeEnable = checkAdsByType(spaceName)
    if (AdsController.getInstance().checkAdsState(spaceName) == StateLoadAd.SUCCESS) {
        preloadCallback?.onLoadDone()
    } else if (AdsController.getInstance().checkAdsState(spaceName) == StateLoadAd.LOADING) {
        //set new call back
        AdsController.getInstance().setPreloadCallback(spaceName, object : PreloadCallback {
            override fun onLoadDone() {
                preloadCallback?.onLoadDone()
            }

            override fun onLoadFail(error: String) {
                super.onLoadFail(error)
                preloadCallback?.onLoadFail(error)
            }

        })
    } else if (includeHasBeenOpened && AdsController.getInstance()
            .checkAdsState(spaceName) == StateLoadAd.HAS_BEEN_OPENED
    ) {
        preloadCallback?.onLoadDone()
    } else if (!isTypeEnable) {
        preloadCallback?.onLoadFail("remote type off")
    } else {
        if (isOn) {
            //tinh toan ad choice
            val newAdChoice: Int? = adChoice
                ?: (config?.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT
                    )
                )?.adChoice)



            AdsController.getInstance()
                .preload(
                    spaceName = spaceName,
                    includeHasBeenOpened = includeHasBeenOpened,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    adChoice = newAdChoice,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    preloadCallback = object : PreloadCallback {
                        override fun onLoadDone() {
                            preloadCallback?.onLoadDone()
                        }

                        override fun onLoadFail(error: String) {
                            super.onLoadFail(error)
                            preloadCallback?.onLoadFail(error)
                        }

                    },
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds
                )
        } else {
            Log.d(
                "TESTERADSEVENT",
                "load failed ads : ads name $spaceName \n config name $configName \\n id ${
                    AdsController.getInstance().getAdsDetail(spaceName)?.adsId ?: "null"
                }\n error : off by config"
            )
            preloadCallback?.onLoadFail("remote off")
        }
    }

}

fun Context.safePreloadAds(
    configName: String,
    spaceName: String,
    //native
    adChoice: Int? = null,
    includeHasBeenOpened: Boolean = false,
    //banner
    positionCollapsibleBanner: String? = null,
    isOneTimeCollapsible: Boolean? = null,
    widthBannerAdaptiveAds: Int? = null,
    preloadCallback: PreloadCallback? = null,
) {
    val config = AdsConstant.listConfigAds[configName]
    val isOn = config?.isOn ?: false
    val isTypeEnable = checkAdsByType(spaceName)
    if (AdsController.getInstance().checkAdsState(spaceName) == StateLoadAd.SUCCESS) {
        preloadCallback?.onLoadDone()
    } else if (AdsController.getInstance().checkAdsState(spaceName) == StateLoadAd.LOADING) {
        //set new call back
        AdsController.getInstance().setPreloadCallback(spaceName, object : PreloadCallback {
            override fun onLoadDone() {
                preloadCallback?.onLoadDone()
            }

            override fun onLoadFail(error: String) {
                super.onLoadFail(error)
                preloadCallback?.onLoadFail(error)
            }

        })
    } else if (includeHasBeenOpened && AdsController.getInstance()
            .checkAdsState(spaceName) == StateLoadAd.HAS_BEEN_OPENED
    ) {
        preloadCallback?.onLoadDone()
    } else if (!isTypeEnable) {
        preloadCallback?.onLoadFail("remote type off")
    } else {
        if (isOn) {
            //tinh toan ad choice
            val newAdChoice: Int? = adChoice
                ?: (config?.getConfigNative(
                    context = this,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT
                    )
                )?.adChoice)



            AdsController.getInstance()
                .preload(
                    spaceName = spaceName,
                    includeHasBeenOpened = includeHasBeenOpened,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    adChoice = newAdChoice,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    preloadCallback = object : PreloadCallback {
                        override fun onLoadDone() {
                            preloadCallback?.onLoadDone()
                        }

                        override fun onLoadFail(error: String) {
                            super.onLoadFail(error)
                            preloadCallback?.onLoadFail(error)
                        }

                    },
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds
                )
        } else {
            Log.d(
                "TESTERADSEVENT",
                "load failed ads : ads name $spaceName \n config name $configName \\n id ${
                    AdsController.getInstance().getAdsDetail(spaceName)?.adsId ?: "null"
                }\n error : off by config"
            )
            preloadCallback?.onLoadFail("remote off")
        }
    }
}

fun Fragment.safePreloadAds(
    listConfigName: List<String>,
    spaceName: String,
    includeHasBeenOpened: Boolean = false,
    positionCollapsibleBanner: String? = null,
    adChoice: Int? = null,
    isOneTimeCollapsible: Boolean? = null,
    preloadCallback: PreloadCallback? = null,
    widthBannerAdaptiveAds: Int? = null
) {
    for (configName in listConfigName) {
        if (AdsConstant.listConfigAds[configName]?.isOn == true) {
            safePreloadAds(
                configName = configName,
                spaceName = spaceName,
                includeHasBeenOpened = includeHasBeenOpened,
                positionCollapsibleBanner = positionCollapsibleBanner,
                adChoice = adChoice,
                isOneTimeCollapsible = isOneTimeCollapsible,
                preloadCallback = preloadCallback,
                widthBannerAdaptiveAds = widthBannerAdaptiveAds
            )
            break
        }
    }
}

fun checkAdsByType(spaceNameAds: String): Boolean {
    if (AdsConstant.disableAllConfig) {
        return false
    }
    val adsDetail = AdsController.getInstance().getAdsDetail(spaceNameAds)
    return when (adsDetail?.adsType) {
        //admob
        AdDef.ADS_TYPE_ADMOB.OPEN_APP -> {
            return AdsConstant.isOpenAppOn
        }

        AdDef.ADS_TYPE_ADMOB.INTERSTITIAL -> {
            return AdsConstant.isInterstitialOn
        }

        AdDef.ADS_TYPE_ADMOB.NATIVE -> {
            return AdsConstant.isNativeOn
        }

        AdDef.ADS_TYPE_ADMOB.NATIVE_FULL_SCREEN -> {
            return AdsConstant.isNativeFullScreenOn
        }

        AdDef.ADS_TYPE_ADMOB.BANNER -> {
            return AdsConstant.isBannerOn
        }

        AdDef.ADS_TYPE_ADMOB.BANNER_ADAPTIVE -> {
            return AdsConstant.isBannerAdaptiveOn
        }

        AdDef.ADS_TYPE_ADMOB.BANNER_LARGE -> {
            return AdsConstant.isBannerLargeOn
        }

        AdDef.ADS_TYPE_ADMOB.BANNER_INLINE -> {
            return AdsConstant.isBannerInlineOn
        }

        AdDef.ADS_TYPE_ADMOB.BANNER_COLLAPSIBLE -> {
            return AdsConstant.isBannerCollapsibleOn
        }

        AdDef.ADS_TYPE_ADMOB.REWARD_VIDEO -> {
            return AdsConstant.isRewardVideoOn
        }

        AdDef.ADS_TYPE_ADMOB.REWARD_INTERSTITIAL -> {
            return AdsConstant.isRewardInterOn
        }

        else -> {
            false
        }
    }
}

fun checkIsPreloadAfterShow(spaceNameConfig: String): Boolean {
    return AdsConstant.listConfigAds[spaceNameConfig]?.isPreloadAfterShow ?: false
}

fun Fragment.blockAppResumeAdsWhenForwardToOtherApp() {
    AdsController.isBlockOpenAds = true
    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(
            source: LifecycleOwner,
            event: Lifecycle.Event
        ) {
            if (event == Lifecycle.Event.ON_STOP) {
                AdsController.isBlockOpenAds = true
                lifecycle.removeObserver(this)
            }
        }
    })
}

fun isConfigType(configName: String, type: String): Boolean {
    return AdsConstant.listConfigAds[configName]?.type == type
}

fun LinkedHashMap<String, StateLoadAd>.isAllHigherAdsFailed(spaceName : String) : Boolean{
    for (result in this) {
        if (spaceName == result.key) {
            return true
        }
        if (result.value != StateLoadAd.LOAD_FAILED) {
            return false
        }
    }
    return true
}

fun setTagAdsBanner(context: Context, viewGroupAds: ViewGroup) {
    val layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.TOP or Gravity.END
    }
    val mTypeface: Typeface? =
        context.let { ResourcesCompat.getFont(it, R.font.font_700) }
    val textView = TextView(context).apply {
        text = "AD"
        background = ContextCompat.getDrawable(context, R.drawable.bg_radius_1)
        val color = Color.parseColor("#FFA800")
        backgroundTintList = ColorStateList.valueOf(color)
        setTextColor(Color.WHITE)
        textSize = 12f
        typeface = mTypeface
        setPadding(6, 1, 6, 1)
    }
    textView.layoutParams = layoutParams
    viewGroupAds.addView(textView)
}

fun drawStrokeOverlay(
    newViewAds: View,
    strokeWidthInPixel: Int,
    strokeColor: String = "#000000"
) {
    val strokeDrawable = object : Drawable() {
        private val paint = Paint().apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            color = Color.parseColor(strokeColor)
            strokeWidth = strokeWidthInPixel.toFloat()
        }

        override fun draw(canvas: Canvas) {
            val halfStroke = strokeWidthInPixel / 2f
            val rect = RectF(
                halfStroke,
                halfStroke,
                bounds.width() - halfStroke,
                bounds.height() - halfStroke
            )
            canvas.drawRect(rect, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }

    // Xóa overlay cũ nếu cần (optional)
    newViewAds.overlay.clear()

    // Đặt bounds và thêm overlay
    newViewAds.post {
        strokeDrawable.setBounds(0, 0, newViewAds.width, newViewAds.height)
        newViewAds.overlay.add(strokeDrawable)
    }
}