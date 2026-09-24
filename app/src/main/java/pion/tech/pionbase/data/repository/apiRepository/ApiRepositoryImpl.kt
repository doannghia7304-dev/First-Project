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
            val allTemplates = response.map { it.customField }

            val categoriesResponse = runCatching { apiInterface.getAppCategory().dataResponse }.getOrNull()
            val selectedCategory = categoriesResponse?.find { it.id == categoryId }
            val categoryName = selectedCategory?.name?.lowercase()?.trim() ?: ""
            val catId = categoryId.lowercase().trim()

            val filtered = allTemplates.filter { dto ->
                val dtoCatId = dto.categoryId?.lowercase()?.trim() ?: ""
                val dtoName = dto.name?.lowercase()?.trim() ?: ""
                val dtoType = dto.templateType?.lowercase()?.trim() ?: ""

                dtoCatId == catId ||
                (categoryName.isNotEmpty() && (dtoCatId == categoryName || dtoName.contains(categoryName) || dtoType.contains(categoryName)))
            }

            if (filtered.isNotEmpty()) {
                filtered
            } else {
                // Phân bổ mẫu theo chỉ số danh mục để mỗi khi chọn danh mục khác nhau, danh sách hình nền lập tức thay đổi tương ứng
                val categoryIndex = categoriesResponse?.indexOfFirst { it.id == categoryId }?.coerceAtLeast(0) ?: 0
                val totalCategories = (categoriesResponse?.size ?: 1).coerceAtLeast(1)
                val chunkSize = (allTemplates.size / totalCategories).coerceAtLeast(1)

                val startIndex = (categoryIndex * chunkSize) % allTemplates.size
                val endIndex = (startIndex + chunkSize).coerceAtMost(allTemplates.size)

                val sliced = allTemplates.subList(startIndex, endIndex)
                if (sliced.isNotEmpty()) sliced else allTemplates
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
