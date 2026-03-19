package pion.tech.pionbase.data.model.appCategory

import com.google.gson.annotations.SerializedName

data class AppCategoryDtoModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
)
