package pion.tech.pionbase.data.model

import com.google.gson.annotations.SerializedName

data class ApiObjectResponseData<T>(
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var dataResponse: T,
    @SerializedName("status")
    var status: Int,
)
