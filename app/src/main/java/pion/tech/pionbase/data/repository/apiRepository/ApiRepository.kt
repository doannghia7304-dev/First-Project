package pion.tech.pionbase.data.repository.apiRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.appCategory.AppCategoryDtoModel
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.util.Result

interface ApiRepository {
    fun getAppCategory(): Flow<Result<List<AppCategoryDtoModel>>>

    fun getTemplateData(categoryId: String): Flow<Result<List<TemplateDtoModel>>>
}
