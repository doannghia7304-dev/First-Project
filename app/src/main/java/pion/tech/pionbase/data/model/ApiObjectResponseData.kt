package pion.tech.pionbase.data.model

import com.google.gson.annotations.SerializedName

data class ApiObjectResponseData<T>(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val dataResponse: T,
    @SerializedName("status")
    val status: Int,
)
