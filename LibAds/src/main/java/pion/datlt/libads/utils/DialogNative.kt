package pion.datlt.libads.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import pion.datlt.libads.AdsController
import pion.datlt.libads.R
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.databinding.DialogNativeAfterInterBinding
import pion.datlt.libads.utils.adsuntils.checkConditionShowAds
import pion.datlt.libads.utils.adsuntils.checkIsPreloadAfterShow
import pion.datlt.libads.utils.adsuntils.safePreloadAds
import pion.datlt.libads.utils.adsuntils.setLastTimeShowInter

object DialogNative {

    private var dialog: Dialog? = null
    private var listener: NativeInterListener? = null
    private var listDismissListener = mutableListOf<() -> Unit>()

    fun show(
        context: Activity,
        configName: String,
        spaceName: String,
        listener: NativeInterListener? = null
    ) {
        listDismissListener.clear()
        this.listener = listener
        if (dialog?.isShowing == true) {
            listener?.onShowNative()
            return
        }

        dialog = Dialog(context)
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_native_after_inter, null)
        dialog?.setContentView(view)
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val binding = DialogNativeAfterInterBinding.bind(view)
        val viewAdsInflateFromXml =
            LayoutInflater.from(context).inflate(R.layout.layout_native_inter_full_screen, null)

        AdsConstant.listConfigAds[configName]?.let { config ->
            //set background color
            runCatching {
                val adViewHolder =
                    viewAdsInflateFromXml.findViewById<ConstraintLayout>(R.id.adViewHolder)
                adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
            }

            //set content text color
            runCatching {
                val headLineText = viewAdsInflateFromXml.findViewById<TextView>(R.id.ad_headline)
                val bodyText = viewAdsInflateFromXml.findViewById<TextView>(R.id.ad_body)
                headLineText.setTextColor(Color.parseColor(config.textContentColor))
                bodyText.setTextColor(Color.parseColor(config.textContentColor))
            }

            //set cta shape
            //set cta color
            //set cta radius
            //set cta ratio
            runCatching {
                val listGradientColor = mutableListOf<Int>()
                config.ctaGradientListColor?.forEach { colorString ->
                    kotlin.runCatching {
                        listGradientColor.add(Color.parseColor(colorString))
                    }
                }

                if (listGradientColor.isEmpty()) {
                    kotlin.runCatching { }
                    if (context != null) {
                        listGradientColor.add(context.getColor(R.color.cta_color))
                        listGradientColor.add(context.getColor(R.color.cta_color))
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
                val ctaButton = viewAdsInflateFromXml.findViewById<TextView>(R.id.ad_call_to_action)
                ctaButton.backgroundTintList = null
                ctaButton.background = gradientDrawable
                ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                if (config.ctaRatio != null) {
                    val ctaButtonParams =
                        ctaButton?.layoutParams as ConstraintLayout.LayoutParams?
                    ctaButtonParams?.dimensionRatio = config.ctaRatio
                    ctaButton?.layoutParams = ctaButtonParams
                }
            }
        }

        AdsController.getInstance().showLoadedAds(
            spaceName = spaceName,
            layoutToAttachAds = binding.adViewGroup,
            viewAdsInflateFromXml = viewAdsInflateFromXml,
            adChoice = AdsConstant.BOTTOM_RIGHT,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    listener?.onShowNative()
                }

                override fun onAdClose() {
                    setLastTimeShowInter(spaceNameConfig = configName)
                    dismiss()
                }

                override fun onAdFailToLoad(messageError: String?) {
                    dismiss()
                }

                override fun onAdClick() {
                    super.onAdClick()
                    listener?.onClickNative()
                    dismiss()
                    setLastTimeShowInter(spaceNameConfig = configName)
                }
            }
        )

        binding.btnCloseNativeFull.setOnClickListener {
            setLastTimeShowInter(spaceNameConfig = configName)
            dismiss()
        }

        if (dialog?.isShowing == false) {
            dialog?.show()
        }

    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
        listener?.onCloseNative()
    }

    fun runWhenNativeDismiss(onDismiss: () -> Unit) {
        listDismissListener.add(onDismiss)
        if (dialog?.isShowing == true) {
            dialog?.setOnDismissListener {
                listDismissListener.forEach {
                    it.invoke()
                }
                listDismissListener.clear()
            }
        } else {
            listDismissListener.forEach {
                it.invoke()
            }
            listDismissListener.clear()
        }
    }

    fun isShowing(): Boolean {
        return dialog?.isShowing == true
    }

}

interface NativeInterListener {
    fun onShowNative()
    fun onCloseNative()
    fun onClickNative()
}