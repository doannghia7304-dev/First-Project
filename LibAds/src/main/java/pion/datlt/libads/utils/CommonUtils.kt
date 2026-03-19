package pion.datlt.libads.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast
import java.io.InputStream

object CommonUtils {

    fun showToastDebug(context: Context?, text: String) {
        if (context != null) {
            if (AdsConstant.isShowToastDebug || AdsConstant.isDebug) {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getStringAssetFile(path: String, activity: Activity): String {
        return activity.assets.open(path).bufferedReader().use { it.readText() }
    }

}