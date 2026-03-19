package pion.tech.pionbase.data.model.template

import com.google.gson.annotations.SerializedName

data class TemplateDtoModel(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("duration")
    val duration: String? = null,
    @SerializedName("video_preview")
    val videoPreview: String? = null,
    @SerializedName("aspect_ratio")
    val aspectRatio: String? = null,
    @SerializedName("music")
    val music: String? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("template_type")
    val templateType: String? = null,
    @SerializedName("image_model")
    val imageModel: String? = null,
    @SerializedName("category_id")
    val categoryId: String? = null,
    @SerializedName("country_id")
    val countryId: String? = null,
)
