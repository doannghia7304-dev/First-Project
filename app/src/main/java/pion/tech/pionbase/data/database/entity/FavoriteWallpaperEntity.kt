package pion.tech.pionbase.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.data.model.template.TemplateUIModel

@Entity(tableName = FavoriteWallpaperEntity.TABLE_NAME)
data class FavoriteWallpaperEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String? = null,
    @ColumnInfo(name = "thumbnail")
    val thumbnail: String? = null,
    @ColumnInfo(name = "image_model")
    val imageModel: String? = null,
    @ColumnInfo(name = "video_preview")
    val videoPreview: String? = null,
    @ColumnInfo(name = "template_type")
    val templateType: String? = null,
    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,
) {
    companion object {
        const val TABLE_NAME = "favorite_wallpapers"
    }
}

fun TemplateDtoModel.toEntity() = FavoriteWallpaperEntity(
    id = this.thumbnail ?: this.imageModel ?: System.currentTimeMillis().toString(),
    name = this.name,
    thumbnail = this.thumbnail,
    imageModel = this.imageModel,
    videoPreview = this.videoPreview,
    templateType = this.templateType,
    categoryId = this.categoryId,
)

fun TemplateUIModel.toEntity() = FavoriteWallpaperEntity(
    id = this.thumbnail ?: this.imageModel ?: System.currentTimeMillis().toString(),
    name = this.name,
    thumbnail = this.thumbnail,
    imageModel = this.imageModel,
    videoPreview = this.videoPreview,
    templateType = this.templateType,
    categoryId = this.categoryId,
)

fun FavoriteWallpaperEntity.toPresentation() = TemplateUIModel(
    name = this.name,
    thumbnail = this.thumbnail,
    imageModel = this.imageModel,
    videoPreview = this.videoPreview,
    templateType = this.templateType,
    categoryId = this.categoryId,
)

fun FavoriteWallpaperEntity.toDto() = TemplateDtoModel(
    name = this.name,
    thumbnail = this.thumbnail,
    imageModel = this.imageModel,
    videoPreview = this.videoPreview,
    templateType = this.templateType,
    categoryId = this.categoryId,
)

fun TemplateUIModel.toDto() = TemplateDtoModel(
    name = this.name,
    thumbnail = this.thumbnail,
    imageModel = this.imageModel,
    videoPreview = this.videoPreview,
    templateType = this.templateType,
    categoryId = this.categoryId,
)
