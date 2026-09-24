package pion.tech.pionbase.data.repository.apiRepository

import pion.tech.pionbase.data.model.template.TemplateDtoModel

object TopicWallpaperProvider {

    /**
     * Lọc danh sách hình nền từ dữ liệu API GitHub dựa trên từ khóa tìm kiếm.
     * Tự động nhận diện từ khóa 'Deep Sea', 'GIF', 'Video', 'Mountain' qua Tên, Đường dẫn, Định dạng.
     */
    fun filterWallpapersByQuery(
        templates: List<TemplateDtoModel>,
        query: String
    ): List<TemplateDtoModel> {
        val q = query.lowercase().trim()
        if (q.isBlank()) return templates

        val isDeepSea = q.contains("deep") || q.contains("sea") || q.contains("ocean") || q.contains("bien")
        val isGifQuery = (q.contains("gif") || q.contains("liquid") || q.contains("abstract")) && !isDeepSea
        val isMountainQuery = (q.contains("mountain") || q.contains("moutain") || q.contains("peak")) && !isDeepSea
        val isCyberpunkQuery = (q.contains("cyberpunk") || q.contains("neon")) && !isDeepSea

        return templates.filter { dto ->
            val name = dto.name?.lowercase() ?: ""
            val catId = dto.categoryId?.lowercase() ?: ""
            val type = dto.templateType?.lowercase() ?: ""
            val image = dto.imageModel?.lowercase() ?: ""
            val thumb = dto.thumbnail?.lowercase() ?: ""
            val video = dto.videoPreview?.lowercase() ?: ""

            val isGifItem = type == "gif" || name.contains("gif") || thumb.endsWith(".gif") || image.endsWith(".gif")

            when {
                // Danh mục Deep Sea: CHỈ lọc hình ảnh/video liên quan tới Biển (Deep Sea)
                isDeepSea -> {
                    !isGifItem && (
                        name.contains("sea") || name.contains("deep") || catId.contains("sea") ||
                        thumb.contains("bien") || image.contains("bien") || video.contains("sea") ||
                        catId == "cate_002" || catId == "cate_007" ||
                        (catId.startsWith("cate_02") && catId != "cate_028" && catId != "cate_029" && catId != "cate_030")
                    )
                }

                // Danh mục GIF: CHỈ lấy các tệp GIF
                isGifQuery -> {
                    isGifItem
                }

                // Danh mục Núi: CHỈ lấy Mountain Peak
                isMountainQuery -> {
                    name.contains("mountain") || name.contains("moutain") || name.contains("peak")
                }

                // Danh mục Cyberpunk: CHỈ lấy Cyberpunk
                isCyberpunkQuery -> {
                    name.contains("cyberpunk") || catId.contains("cyberpunk")
                }

                else -> {
                    name.contains(q) || catId.contains(q) || type.contains(q) || image.contains(q) || thumb.contains(q) || video.contains(q)
                }
            }
        }
    }
}
