package pion.datlt.libads.model

import com.google.gson.annotations.SerializedName

class AdsChild {

    @SerializedName("network")
    var network: String = "google"

    @SerializedName("spaceName")
    var spaceName: String = "null"

    @SerializedName("adsType")
    var adsType: String = "null"

    @SerializedName("id")
    var adsId: String = "null"

    @SerializedName("placementId")
    var placementId: String = "null"

    @SerializedName("priority")
    var priority: Int = -1


    override fun toString(): String {
        return "AdsChild(network='$network', spaceName='$spaceName', adsType='$adsType', adsId='$adsId', priority=$priority)"
    }
}