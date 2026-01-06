package com.example.libiap.model

import com.google.gson.annotations.SerializedName

data class IapIdModel(

    @SerializedName("productId")
    var idProduct: String = "null",

    @SerializedName("type")
    var type: String = "null",

    @SerializedName("listBasePlan")
    var listBasePlan: List<BasePlanIdModel> = listOf()

    )

