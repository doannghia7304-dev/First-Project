package pion.tech.pionbase.data.repository.apiRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.data.model.appCategory.AppCategoryDtoModel
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.data.remote.ApiInterface
import pion.tech.pionbase.util.Result

class ApiRepositoryImpl(
    private val apiInterface: ApiInterface,
) : ApiRepository {
    override fun getAppCategory(): Flow<Result<List<AppCategoryDtoModel>>> =
        flow<Result<List<AppCategoryDtoModel>>> {
            emit(Result.Success(apiInterface.getAppCategory().dataResponse))
        }.catch {
            emit(Result.Error(it))
        }.flowOn(Dispatchers.IO)

    override fun getTemplateData(categoryId: String): Flow<Result<List<TemplateDtoModel>>> =
        flow<Result<List<TemplateDtoModel>>> {
            emit(Result.Success(apiInterface.getAllTemplate(categoryId).dataResponse.map { it.customField }))
        }.catch {
            emit(Result.Error(it))
        }.flowOn(Dispatchers.IO)
}
