package pion.tech.pionbase.data.repository.apiRepository

import pion.tech.pionbase.data.model.template.TemplateDtoModel

object TopicWallpaperProvider {

    /**
     * Lọc danh sách hình nền trực tiếp từ dữ liệu API GitHub trả về dựa trên từ khóa tìm kiếm.
     * Không sử dụng dữ liệu cứng (hardcoded), toàn bộ dữ liệu mẫu đều đến từ GitHub API.
     */
    fun filterWallpapersByQuery(
        templates: List<TemplateDtoModel>,
        query: String
    ): List<TemplateDtoModel> {
        val q = query.lowercase().trim()
        if (q.isBlank()) return templates

        val filtered = templates.filter { dto ->
            dto.name?.lowercase()?.contains(q) == true ||
            dto.categoryId?.lowercase()?.contains(q) == true ||
            dto.templateType?.lowercase()?.contains(q) == true ||
            dto.imageModel?.lowercase()?.contains(q) == true ||
            dto.thumbnail?.lowercase()?.contains(q) == true
        }

        return if (filtered.isNotEmpty()) {
            filtered
        } else {
            templates
        }
    }
}
