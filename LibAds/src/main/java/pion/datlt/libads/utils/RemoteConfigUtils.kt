package pion.datlt.libads.utils

import android.content.Context
import pion.datlt.libads.R

fun readRawTextFile(context: Context, resId: Int): String {
    return context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
}

fun getRemoteConfigDefaults(context: Context): Map<String, Any> {
    val longText = readRawTextFile(context, R.raw.longtext) // đọc nội dung file raw/longtext.txt
    val longText2 = readRawTextFile(context, R.raw.longtext2) // đọc nội dung file raw/longtext.txt

    return mapOf(
        "admob_id_inapp" to longText2, // gán nội dung vào admob_id1
        "config_show_ads_inapp" to longText,
        "admob_id" to longText2, // gán nội dung vào admob_id1
        "config_show_ads" to longText
    )
}