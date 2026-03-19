package pion.datlt.libads.model

import com.google.gson.annotations.SerializedName

class Ads {
    @SerializedName("network")
    var network : String = "google"

    @SerializedName("priority")
    var priority:Int = 0

    @SerializedName("appId")
    var appId:String = ""

    @SerializedName("package")
    var packageName :String = ""

    @SerializedName("listAds")
    var listAdsChild:ArrayList<AdsChild> = ArrayList()

    override fun toString(): String {
        return "Ads(appId='$appId', packetName='$packageName', listAdsChild=$listAdsChild)"
    }
}