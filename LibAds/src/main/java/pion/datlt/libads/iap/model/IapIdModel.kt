package pion.datlt.libads.iap.model

import com.google.gson.annotations.SerializedName

data class IapIdModel(
    @SerializedName("productId")
    var idProduct: String = "null",
    @SerializedName("type")
    var type: String = "null",
    @SerializedName("listBasePlan")
    var listBasePlan: List<BasePlanIdModel> = listOf(),
)
