package pion.datlt.libads.utils.adsuntils

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import pion.datlt.libads.AdsActivity
import pion.datlt.libads.AdsController
import pion.datlt.libads.R
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.ConfigNative
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.StateLoadAd

fun Fragment.showAdsNativeFullScreen(
    configName: String,
    listSpaceName: List<String>,
    includeHasBeenOpened: Boolean = false,
    adChoice: Int? = null,
    isLandscape: Boolean = false,
    viewAds: View? = null,
    viewGroupAds: ViewGroup,
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, configName)) {
        //dieu chinh lai kich thuoc
        var newAdChoice = adChoice
        var newViewAds = viewAds
        AdsConstant.listConfigAds[configName]?.let { config ->
            config.getConfigNative(
                context = context,
                isLandscape = isLandscape,
                default = ConfigNative(
                    adChoice = AdsConstant.TOP_LEFT,
                    viewAds = LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_full_screen, null)
                )
            ).let { configNative ->

                if (newAdChoice == null) {
                    newAdChoice = configNative.adChoice
                }

                if (newViewAds == null) {
                    if (configNative.viewAds?.tag == "fullscreen") {
                        newViewAds = configNative.viewAds
                    } else {
                        newViewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_full_screen, null)
                    }
                }
            }

            //set cta shape
            //set cta color
            //set cta radius
            //set cta ratio
            runCatching {
                if (newViewAds != null) {
                    val listGradientColor = mutableListOf<Int>()
                    config.ctaGradientListColor?.forEach { colorString ->
                        kotlin.runCatching {
                            listGradientColor.add(Color.parseColor(colorString))
                        }
                    }

                    if (listGradientColor.isEmpty()) {
                        if (context != null) {
                            listGradientColor.add(context!!.getColor(R.color.cta_color))
                            listGradientColor.add(context!!.getColor(R.color.cta_color))
                        } else {
                            listGradientColor.add(Color.parseColor("#3ADB41"))
                            listGradientColor.add(Color.parseColor("#3ADB41"))
                        }
                    } else if (listGradientColor.size == 1) {
                        listGradientColor.add(listGradientColor[0])
                    }

                    val gradientDrawable = GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        listGradientColor.toIntArray()
                    ).apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            config.ctaConnerRadius.toFloat(),
                            Resources.getSystem().displayMetrics
                        )
                    }
                    val ctaButton = newViewAds!!.findViewById<TextView>(R.id.ad_call_to_action)
                    ctaButton.backgroundTintList = null
                    ctaButton.background = gradientDrawable
                    ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                    if (config.ctaRatio != null) {
                        val ctaButtonParams =
                            ctaButton?.layoutParams as ConstraintLayout.LayoutParams?
                        ctaButtonParams?.dimensionRatio = config.ctaRatio
                        ctaButton?.layoutParams = ctaButtonParams
                    }
                    if (config.ctaAnimationSpeed > 0) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            YoYo.with(Techniques.Pulse)
                                .duration(config.ctaAnimationSpeed)
                                .repeat(Animation.INFINITE)
                                .playOn(ctaButton)
                        }, 500)
                    }
                }
            }

            //set background color
            runCatching {
                if (newViewAds != null) {
                    val adViewHolder =
                        newViewAds!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                    adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    newViewAds!!.setBackgroundColor(Color.parseColor(config.backGroundColor))
                }
            }

            //set content text color
            runCatching {
                if (newViewAds != null) {
                    val headLineText =
                        newViewAds!!.findViewById<TextView>(R.id.ad_headline)
                    val bodyText =
                        newViewAds!!.findViewById<TextView>(R.id.ad_body)
                    headLineText.setTextColor(Color.parseColor(config.textContentColor))
                    bodyText.setTextColor(Color.parseColor(config.textContentColor))
                }
            }
        }

        val mapResult = LinkedHashMap<String, StateLoadAd>()
        var isPriorityTimeOut = false

        fun showAds(spaceName: String) {
            mapResult[spaceName] = StateLoadAd.HAS_BEEN_OPENED
            AdsController.getInstance().showLoadedAds(
                spaceName = spaceName,
                includeHasBeenOpened = includeHasBeenOpened,
                viewGroupAds = viewGroupAds,
                viewAds = newViewAds,
                adChoice = adChoice,
                adCallback = object : AdCallback {
                    override fun onAdShow() {
                        if (checkIsPreloadAfterShow(spaceNameConfig = configName)) {
                            safePreloadAds(
                                configName = configName,
                                spaceName = spaceName,
                                includeHasBeenOpened = includeHasBeenOpened,
                                adChoice = adChoice
                            )
                        }
                    }

                    override fun onAdClose() {}

                    override fun onAdFailToLoad(messageError: String?) {
                        if (checkIsPreloadAfterShow(spaceNameConfig = configName)) {
                            safePreloadAds(
                                configName = configName,
                                spaceName = spaceName,
                                includeHasBeenOpened = includeHasBeenOpened,
                                adChoice = adChoice
                            )
                        }
                    }

                    override fun onAdOff() {}

                    override fun onAdClick() {
                        AdsController.isBlockOpenAds = true
                        onAdsClick?.invoke()
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
                adChoice = newAdChoice,
                includeHasBeenOpened = includeHasBeenOpened,
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