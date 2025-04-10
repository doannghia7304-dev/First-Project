package pion.tech.pionbase.framework.network

import com.google.gson.annotations.SerializedName

data class ApiObjectResponse<T>(
    @SerializedName("message") var message: String,
    @SerializedName("data") var dataResponse: T,
    @SerializedName("status") var status: Int
)