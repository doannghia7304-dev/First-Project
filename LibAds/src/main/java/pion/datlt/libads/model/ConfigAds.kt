package pion.datlt.libads.model

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.annotations.SerializedName
import pion.datlt.libads.R
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant

class ConfigAds {
    @SerializedName("configName")
    val configName: String = ""

    @SerializedName("isOn")
    val isOn: Boolean = false

    @SerializedName("type")
    val type: String = "interstitial"


    @SerializedName("network")
    val network: String = AdDef.NETWORK.GOOGLE


    //inter

    @SerializedName("timeDelayShowInter")
    val timeDelayShowInter: Int? = null

    @SerializedName("isShowNativeAfterInter")
    val isShowNativeAfterInter: Boolean = false


    //native

    @SerializedName("ctaGradientListColor")
    val ctaGradientListColor: List<String>? = null

    @SerializedName("textCTAColor")
    val textCTAColor: String = "#FFFFFF"

    @SerializedName("ctaRatio")
    val ctaRatio: String? = null

    @SerializedName("ctaConnerRadius")
    val ctaConnerRadius: Int = 10

    @SerializedName("layoutTemplate")
    val layoutTemplate: String = small_icon_ctaright

    @SerializedName("backGroundColor")
    val backGroundColor: String = "#E8E6E6"

    @SerializedName("textContentColor")
    val textContentColor: String = "#444444"

    @SerializedName("isPreloadAfterShow")
    val isPreloadAfterShow: Boolean = false

    @SerializedName("isCloseWhenClick")
    val isCloseWhenClick: Boolean = false

    @SerializedName("isCloseWhenClickNativeCollapsible")
    val isCloseWhenClickNativeCollapsible: Boolean = true

    @SerializedName("ctaAnimationSpeed")
    val ctaAnimationSpeed: Long = 0L

    @SerializedName("nativeStrokeWidth")
    val nativeStrokeWidth: Float = 0f

    @SerializedName("nativeStrokeColor")
    val nativeStrokeColor: String = "#000000"

    @SerializedName("timeShowNativeCollapsibleAfterClose")
    val timeShowNativeCollapsibleAfterClose: Int = 5


    fun getConfigNative(
        context: Context?,
        isLandscape : Boolean = false,
        default: ConfigNative
    ): ConfigNative {
        val adChoice: Int
        var ratio: String? = null
        val viewAds = when (this.layoutTemplate) {

            small_icon_ctaright -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_small_icon_ctaright, null)
                }
            }

            small_ctaright -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_small_ctaright, null)
                }
            }

            Medium1_icontop_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium1_icontop_ctabot, null)
                }
            }

            medium3_icon_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium3_icon_ctabot, null)
                }
            }

            medium3_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium3_ctabot, null)
                }
            }

            Medium2_icon_ctatop -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium2_icon_ctatop, null)
                }
            }

            Medium2_icon_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium2_icon_ctabot, null)
                }
            }

            medium3_ctatop -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium3_ctatop, null)
                }
            }

            Larger_iconbot_cta_bot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_larger_iconbot_cta_bot, null)
                }
            }

            Larger_icontop_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_larger_icontop_ctabot, null)
                }
            }

            Larger_iconframe_cta_bot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_larger_iconframe_cta_bot, null)
                }
            }

            Medium1_icontop_ctabot_collapsible -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium1_icontop_ctabot_collapsible, null)
                }
            }

            medium2_icon_ctabot_collapsible -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium2_icon_ctabot_collapsible, null)
                }
            }

            medium2_ctabot_collapsible -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium2_ctabot_collapsible, null)
                }
            }

            small_icon_ctaright_collapsible -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_small_icon_ctaright_collapsible, null)
                }
            }

            nativefull_media_icon_cta -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_nativefull_media_icon_cta, null)
                }
            }

            nativefull_media_iconframe_cta -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_nativefull_media_iconframe_cta, null)
                }
            }

            nativefull_iconframe_media_cta -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_nativefull_iconframe_media_cta, null)
                }
            }

            nativefull_media_iconmiddle_cta -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_nativefull_media_iconmiddle_cta, null)
                }
            }

            nativefull_icon_media_cta -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_nativefull_icon_media_cta, null)
                }
            }

            nativefull_noicon_media_cta -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_nativefull_noicon_media_cta, null)
                }
            }

            nativefull_media916_cta_icon -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_nativefull_media916_cta_icon, null)
                }
            }

            nativefull_media34_cta_icon -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_nativefull_media34_cta_icon, null)
                }
            }

            nativefull_media34_titleleft_ctaright -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_nativefull_media34_titleleft_ctaright, null)
                }
            }

            nativefull_media34__ctaleft_titleright -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_nativefull_media34__ctaleft_titleright, null)
                }
            }

            medium_medialeft_iconright_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_medialeft_iconright_ctabot, null)
                }
            }

            medium_medialeft_noiconright_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_medialeft_noiconright_ctabot, null)
                }
            }

            medium_medialeft_iconright_ctatop -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_medialeft_iconright_ctatop, null)
                }
            }

            medium_medialeft_noiconright_ctatop -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_medialeft_noiconright_ctatop, null)
                }
            }


            medium_medialeft_iconright_ctaright -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_medialeft_iconright_ctaright, null)
                }
            }


            medium_medialeft_noiconright_ctaright -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_medialeft_noiconright_ctaright, null)
                }
            }


            medium_mediaright_iconleft_ctaleft -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_mediaright_iconleft_ctaleft, null)
                }
            }


            medium_mediaright_noiconleft_ctaleft -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_mediaright_noiconleft_ctaleft, null)
                }
            }


            medium_icontop_bodymidd_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_icontop_bodymidd_ctabot, null)
                }
            }


            medium_icontop_ctamidd -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_icontop_ctamidd, null)
                }
            }


            medium_medialeft_icontop_ctaright -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_medialeft_icontop_ctaright, null)
                }
            }

            medium_medialeft_noicontop_ctaright -> {
                adChoice = AdsConstant.TOP_RIGHT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_medialeft_noicontop_ctaright, null)
                }
            }


            lager_mediabot_iconleft_ctaright -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_lager_mediabot_iconleft_ctaright, null)
                }
            }


            lager_mediabot_noiconleft_ctaright -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_lager_mediabot_noiconleft_ctaright, null)
                }
            }


            larger_iconmidd_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_larger_iconmidd_ctabot, null)
                }
            }


            larger_noiconmidd_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_larger_noiconmidd_ctabot, null)
                }
            }


            medium_icontop_bodymidd_ctabot_collapsible -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_icontop_bodymidd_ctabot_collapsible, null)
                }
            }


            medium_icontop_ctamidd_collapsible -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_medium_icontop_ctamidd_collapsible, null)
                }
            }

            else -> {
                adChoice = default.adChoice
                default.viewAds
            }
        }

        runCatching {
            if (viewAds != null && type == AdDef.ADS_TYPE_ADMOB.NATIVE) {
                val ctaButton = viewAds.findViewById<TextView>(R.id.ad_call_to_action)
                if (ctaRatio != null) {
                    val ctaButtonParams = ctaButton?.layoutParams as ConstraintLayout.LayoutParams?
                    ctaButtonParams?.dimensionRatio = ctaRatio
                    ctaButton?.layoutParams = ctaButtonParams
                }

                ratio = hashRatio[configName]
                if (ratio == null) {
                    val sizeAds = calculateLayoutViewSize(viewAds , isLandscape)
                    ratio = "${sizeAds.first}:${sizeAds.second}"
                }
                hashRatio[configName] = ratio

                Log.d("CHECKHASHRATIO", "getConfigNative: $hashRatio")

            }
        }

        return ConfigNative(
            adChoice = adChoice,
            viewAds = viewAds,
            ratio = ratio
        )
    }

    private fun calculateLayoutViewSize(adsView: View , isLandscape : Boolean): Pair<Int, Int> {
        val systemResource = Resources.getSystem()
        val displayMetrics = systemResource.displayMetrics
        val orientation = systemResource.configuration.orientation
        val availableWidth = if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            //man hinh dang o chieu ngang
            if (isLandscape){
                displayMetrics.widthPixels
            }else{
                displayMetrics.heightPixels
            }
        }else{
            //man hinh dang o chieu doc
            if (isLandscape){
                displayMetrics.heightPixels
            }else{
                displayMetrics.widthPixels
            }
        }
        val widthSpec = View.MeasureSpec.makeMeasureSpec(availableWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        adsView.measure(widthSpec, heightSpec)
        val measuredWidth = adsView.measuredWidth
        val measuredHeight = adsView.measuredHeight
        adsView.clearAnimation()
        return measuredWidth to measuredHeight
    }

    companion object {
        val hashRatio = hashMapOf<String, String?>()

        //small
        const val small_icon_ctaright = "small_icon_ctaright"
        const val small_ctaright = "small_ctaright"

        //medium
        const val Medium1_icontop_ctabot = "Medium1_icontop_ctabot"
        const val medium3_icon_ctabot = "medium3_icon_ctabot"
        const val medium3_ctabot = "medium3_ctabot"
        const val Medium2_icon_ctatop = "Medium2_icon_ctatop"
        const val Medium2_icon_ctabot = "Medium2_icon_ctabot"
        const val medium3_ctatop = "medium3_ctatop"
        const val medium_medialeft_iconright_ctabot = "medium_medialeft_iconright_ctabot"
        const val medium_medialeft_noiconright_ctabot = "medium_medialeft_noiconright_ctabot"
        const val medium_medialeft_iconright_ctatop = "medium_medialeft_iconright_ctatop"
        const val medium_medialeft_noiconright_ctatop = "medium_medialeft_noiconright_ctatop"
        const val medium_medialeft_iconright_ctaright = "medium_medialeft_iconright_ctaright"
        const val medium_medialeft_noiconright_ctaright = "medium_medialeft_noiconright_ctaright"
        const val medium_mediaright_iconleft_ctaleft = "medium_mediaright_iconleft_ctaleft"
        const val medium_mediaright_noiconleft_ctaleft = "medium_mediaright_noiconleft_ctaleft"
        const val medium_icontop_bodymidd_ctabot = "medium_icontop_bodymidd_ctabot"
        const val medium_icontop_ctamidd = "medium_icontop_ctamidd"
        const val medium_medialeft_icontop_ctaright = "medium_medialeft_icontop_ctaright"
        const val medium_medialeft_noicontop_ctaright = "medium_medialeft_noicontop_ctaright"


        //large
        const val Larger_iconbot_cta_bot = "Larger_iconbot_cta_bot"
        const val Larger_icontop_ctabot = "Larger_icontop_ctabot"
        const val Larger_iconframe_cta_bot = "Larger_iconframe_cta_bot"
        const val lager_mediabot_iconleft_ctaright = "lager_mediabot_iconleft_ctaright"
        const val lager_mediabot_noiconleft_ctaright = "lager_mediabot_noiconleft_ctaright"
        const val larger_iconmidd_ctabot = "larger_iconmidd_ctabot"
        const val larger_noiconmidd_ctabot = "larger_noiconmidd_ctabot"

        //collapsible
        const val Medium1_icontop_ctabot_collapsible = "Medium1_icontop_ctabot_collapsible"
        const val medium2_icon_ctabot_collapsible = "medium2_icon_ctabot_collapsible"
        const val medium2_ctabot_collapsible = "medium2_ctabot_collapsible"
        const val small_icon_ctaright_collapsible = "small_icon_ctaright_collapsible"
        const val medium_icontop_bodymidd_ctabot_collapsible = "medium_icontop_bodymidd_ctabot_collapsible"
        const val medium_icontop_ctamidd_collapsible = "medium_icontop_ctamidd_collapsible"


        //native full
        const val nativefull_media_icon_cta = "nativefull_media_icon_cta"
        const val nativefull_media_iconframe_cta = "nativefull_media_iconframe_cta"
        const val nativefull_iconframe_media_cta = "nativefull_iconframe_media_cta"
        const val nativefull_media_iconmiddle_cta = "nativefull_media_iconmiddle_cta"
        const val nativefull_icon_media_cta = "nativefull_icon_media_cta"
        const val nativefull_noicon_media_cta = "nativefull_noicon_media_cta"
        const val nativefull_media916_cta_icon = "nativefull_media916_cta_icon"
        const val nativefull_media34_cta_icon = "nativefull_media34_cta_icon"
        const val nativefull_media34_titleleft_ctaright = "nativefull_media34_titleleft_ctaright"
        const val nativefull_media34__ctaleft_titleright = "nativefull_media34__ctaleft_titleright"

    }
}