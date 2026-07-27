package pion.tech.pionbase.data.repository.apiRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.appCategory.AppCategoryDtoModel
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.data.remote.ApiInterface
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result

class ApiRepositoryImpl(
    private val apiInterface: ApiInterface,
) : BaseRepository(), ApiRepository {
    override fun getAppCategory(): Flow<Result<List<AppCategoryDtoModel>>> = executeDataCall {
        apiInterface.getAppCategory().dataResponse
    }

    override fun getTemplateData(categoryId: String): Flow<Result<List<TemplateDtoModel>>> =
        executeDataCall {
            apiInterface.getAllTemplate(categoryId).dataResponse.map { it.customField }
        }
}
