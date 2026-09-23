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
            val response = apiInterface.getAllTemplate(categoryId).dataResponse
            val filtered = response.filter { it.customField.categoryId == categoryId }
            if (filtered.isNotEmpty()) {
                filtered.map { it.customField }
            } else {
                // Fallback thông minh: Nếu không khớp categoryId hoặc chưa phân loại, hiển thị toàn bộ template để màn hình Home không bị trống
                response.map { it.customField }
            }
        }

    override fun searchWallpapers(query: String): Flow<Result<List<TemplateDtoModel>>> =
        executeDataCall {
            val githubResponse = apiInterface.getAllTemplate("").dataResponse
            val githubTemplates = githubResponse.map { it.customField }
            
            if (query.isBlank()) {
                githubTemplates
            } else {
                TopicWallpaperProvider.filterWallpapersByQuery(githubTemplates, query)
            }
        }
}
