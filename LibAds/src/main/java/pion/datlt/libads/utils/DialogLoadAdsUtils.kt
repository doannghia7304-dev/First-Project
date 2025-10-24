package pion.datlt.libads.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pion.datlt.libads.R

class DialogLoadAdsUtils {

    companion object {
        var dialog: Dialog? = null

        private var mSelf: DialogLoadAdsUtils? = null

        fun getInstance(): DialogLoadAdsUtils {
            if (mSelf == null) {
                mSelf = DialogLoadAdsUtils()
            }
            return mSelf!!
        }
    }

    private val closeDialogRunnable = Runnable {
        getInstance().hideDialogLoadingAds()
    }
    private val handler = Handler(Looper.getMainLooper())

    private val timeOutCloseDialog = 7000L

    fun showDialogLoadingAds(activity: Activity?, isScreenView: Boolean = true) {
        if (dialog != null && dialog?.isShowing == true) {
            kotlin.runCatching {
                dialog?.dismiss()
                dialog = null
            }
        }

        activity?.let {
            dialog = Dialog(it)

            val view: View = if (isScreenView) {
                LayoutInflater.from(it).inflate(R.layout.screen_loading_inter_ads, null)
            } else {
                LayoutInflater.from(it).inflate(R.layout.dialog_loading_inter_ads, null)
            }

            dialog?.setContentView(view)
            dialog?.setCancelable(false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )


            if (dialog != null && !dialog!!.isShowing) {
                try {
                    Log.d("CHECKDIALOG", "showDialogLoadingAds: dialog!!.show()")
                    dialog!!.show()
                    handler.postDelayed(closeDialogRunnable, timeOutCloseDialog)
                } catch (e: Exception) {

                }
            }
        }
    }

    fun hideDialogLoadingAds(timeDelay: Long = 500L) {
        handler.removeCallbacks(closeDialogRunnable)
        if (timeDelay <= 0L) {
            try {
                if (dialog != null && dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            } catch (e: Exception) {

            }
            dialog = null
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    if (dialog != null && dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                } catch (e: Exception) {

                }
                dialog = null
            }, timeDelay)
        }
    }


}