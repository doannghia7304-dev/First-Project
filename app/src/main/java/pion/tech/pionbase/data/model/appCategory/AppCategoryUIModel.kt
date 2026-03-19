package pion.tech.pionbase.data.model.appCategory

data class AppCategoryUIModel(
    val id: String,
    val name: String,
)

fun AppCategoryDtoModel.toPresentation(): AppCategoryUIModel = AppCategoryUIModel(id = this.id, name = this.name)
