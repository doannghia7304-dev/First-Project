package pion.datlt.libads.utils

import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import pion.datlt.libads.AdsController
import pion.datlt.libads.R
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.databinding.DialogNativeAfterInterBinding
import pion.datlt.libads.model.ConfigNative
import pion.datlt.libads.utils.adsuntils.checkConditionShowAds
import pion.datlt.libads.utils.adsuntils.setLastTimeShowInter

object DialogNative {

    private var dialog: Dialog? = null
    private var listener: NativeInterListener? = null
    private var listDismissListener = mutableListOf<() -> Unit>()


    fun show(
        context: Activity,
        configName: String,
        spaceName: String,
        isLandscape: Boolean = false,
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

        var adChoice: Int? = null
        var viewAds: View? = null

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

                if (adChoice == null) {
                    adChoice = configNative.adChoice
                }

                if (viewAds == null) {
                    if (configNative.viewAds?.tag == "fullscreen") {
                        viewAds = configNative.viewAds
                    } else {
                        viewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_full_screen, null)
                    }
                }

            }

            //set cta shape
            //set cta color
            //set cta radius
            //set cta ratio
            runCatching {
                if (viewAds != null) {
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
                    val ctaButton =
                        viewAds!!.findViewById<TextView>(R.id.ad_call_to_action)
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

            //set background color
            runCatching {
                if (viewAds != null) {
                    val adViewHolder =
                        viewAds!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                    adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    viewAds!!.setBackgroundColor(Color.parseColor(config.backGroundColor))
                }
            }

            //set content text color
            runCatching {
                if (viewAds != null) {
                    val headLineText =
                        viewAds!!.findViewById<TextView>(R.id.ad_headline)
                    val bodyText =
                        viewAds!!.findViewById<TextView>(R.id.ad_body)
                    headLineText.setTextColor(Color.parseColor(config.textContentColor))
                    bodyText.setTextColor(Color.parseColor(config.textContentColor))
                }
            }
        }

        AdsController.getInstance().showLoadedAds(
            spaceName = spaceName,
            viewGroupAds = binding.adViewGroup,
            viewAds = viewAds ?: LayoutInflater.from(context)
                .inflate(R.layout.layout_native_inter_full_screen, null),
            adChoice = adChoice ?: AdsConstant.BOTTOM_RIGHT,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    listener?.onShowNative()
                }

                override fun onAdClose() {
                    setLastTimeShowInter()
                    dismiss()
                }

                override fun onAdFailToLoad(messageError: String?) {
                    dismiss()
                }

                override fun onAdClick() {
                    super.onAdClick()
                    dismiss()
                    setLastTimeShowInter()
                }
            }
        )

        drawButtonClose(context, binding.root).setOnClickListener {
            setLastTimeShowInter()
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

    private fun drawButtonClose(context: Activity, frameLayout: FrameLayout): ImageView {
        val imageView = ImageView(context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                context.resources.getDimensionPixelSize(htkien.autodimens.R.dimen._24dp),
                context.resources.getDimensionPixelSize(htkien.autodimens.R.dimen._24dp)
            ).apply {

                if (AdsConstant.positionCloseNativeAfterInter == AdsConstant.NativeInterClosePosition.LEFT) {
                    gravity = Gravity.START
                } else if (AdsConstant.positionCloseNativeAfterInter == AdsConstant.NativeInterClosePosition.CENTER) {
                    gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                } else {
                    gravity = Gravity.END
                }

                val marginLeft =
                    context.resources.getDimensionPixelSize(htkien.autodimens.R.dimen._16dp)
                val marginRight =
                    context.resources.getDimensionPixelSize(htkien.autodimens.R.dimen._16dp)
                val marginTop =
                    context.resources.getDimensionPixelSize(htkien.autodimens.R.dimen._10dp)
                val marginBottom = 0
                setMargins(marginLeft, marginTop, marginRight, marginBottom)
            }

            background = ContextCompat.getDrawable(context, R.drawable.bg_radius_100)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ECEBEB"))

            setPadding(
                context.resources.getDimensionPixelSize(htkien.autodimens.R.dimen._6dp),
                context.resources.getDimensionPixelSize(htkien.autodimens.R.dimen._6dp),
                context.resources.getDimensionPixelSize(htkien.autodimens.R.dimen._6dp),
                context.resources.getDimensionPixelSize(htkien.autodimens.R.dimen._6dp),
            )

            setImageResource(R.drawable.ic_close)
            imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
        }

        // Thêm ImageView vào FrameLayout
        frameLayout.addView(imageView)
        return imageView
    }

}

interface NativeInterListener {
    fun onShowNative()
    fun onCloseNative()
}