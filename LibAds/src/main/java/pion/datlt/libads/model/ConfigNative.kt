package pion.datlt.libads.model

import android.view.View
import android.view.ViewGroup

data class ConfigNative(
    val adChoice : Int,
    val viewAds : View? = null,
    val ratio : String? = null
)