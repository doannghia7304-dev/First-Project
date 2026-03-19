package pion.tech.pionbase.data.model.template

import com.google.gson.annotations.SerializedName

data class TemplateResponseDtoModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("is_pro")
    val isPro: Boolean? = null,
    @SerializedName("custom_fields")
    val customField: TemplateDtoModel,
)
