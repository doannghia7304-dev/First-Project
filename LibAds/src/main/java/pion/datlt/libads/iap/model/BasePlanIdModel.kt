package com.example.libiap.model

import com.google.gson.annotations.SerializedName

data class BasePlanIdModel(
    @SerializedName("basePlanId")
    var basePlanId: String = "null",

    @SerializedName("offerId")
    var offerId: String? = null
)