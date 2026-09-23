package pion.tech.pionbase.data.repository.apiRepository

import pion.tech.pionbase.data.model.template.TemplateDtoModel

object TopicWallpaperProvider {

    private val themeMap = mapOf(
        "cyberpunk" to listOf(
            TemplateDtoModel(
                name = "Cyberpunk Neon City 4K",
                thumbnail = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=1000",
                templateType = "image",
                categoryId = "cyberpunk"
            ),
            TemplateDtoModel(
                name = "Futuristic Tokyo Night",
                thumbnail = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?q=80&w=1000",
                templateType = "image",
                categoryId = "cyberpunk"
            ),
            TemplateDtoModel(
                name = "Neon Cyberpunk Alley",
                thumbnail = "https://images.unsplash.com/photo-1519501025264-65ba15a82390?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1519501025264-65ba15a82390?q=80&w=1000",
                templateType = "image",
                categoryId = "cyberpunk"
            ),
            TemplateDtoModel(
                name = "Neon Gaming World",
                thumbnail = "https://images.unsplash.com/photo-1511512578047-dfb367046420?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1511512578047-dfb367046420?q=80&w=1000",
                templateType = "image",
                categoryId = "cyberpunk"
            ),
            TemplateDtoModel(
                name = "Cyberpunk Synthwave",
                thumbnail = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?q=80&w=1000",
                templateType = "image",
                categoryId = "cyberpunk"
            )
        ),
        "deep sea" to listOf(
            TemplateDtoModel(
                name = "Deep Ocean Abyss 4K",
                thumbnail = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?q=80&w=1000",
                templateType = "image",
                categoryId = "deep sea"
            ),
            TemplateDtoModel(
                name = "Underwater Sun Rays",
                thumbnail = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=1000",
                templateType = "image",
                categoryId = "deep sea"
            ),
            TemplateDtoModel(
                name = "Deep Sea Coral Reef",
                thumbnail = "https://images.unsplash.com/photo-1518837695005-2083093ee35b?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1518837695005-2083093ee35b?q=80&w=1000",
                templateType = "image",
                categoryId = "deep sea"
            ),
            TemplateDtoModel(
                name = "Bioluminescent Dark Ocean",
                thumbnail = "https://images.unsplash.com/photo-1498855926480-d98e83099315?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1498855926480-d98e83099315?q=80&w=1000",
                templateType = "image",
                categoryId = "deep sea"
            )
        ),
        "sea" to listOf(
            TemplateDtoModel(
                name = "Deep Ocean Abyss 4K",
                thumbnail = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?q=80&w=1000",
                templateType = "image",
                categoryId = "deep sea"
            )
        ),
        "space" to listOf(
            TemplateDtoModel(
                name = "Milky Way Galaxy HD",
                thumbnail = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?q=80&w=1000",
                templateType = "image",
                categoryId = "space"
            ),
            TemplateDtoModel(
                name = "Earth from Space 4K",
                thumbnail = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=1000",
                templateType = "image",
                categoryId = "space"
            )
        ),
        "anime" to listOf(
            TemplateDtoModel(
                name = "Fantasy Anime Sky 4K",
                thumbnail = "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=1000",
                templateType = "image",
                categoryId = "anime"
            ),
            TemplateDtoModel(
                name = "Anime Street Aesthetic",
                thumbnail = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=1000",
                templateType = "image",
                categoryId = "anime"
            ),
            TemplateDtoModel(
                name = "Anime Art World",
                thumbnail = "https://images.unsplash.com/photo-1578632767115-351597cf2477?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1578632767115-351597cf2477?q=80&w=1000",
                templateType = "image",
                categoryId = "anime"
            )
        ),
        "cars" to listOf(
            TemplateDtoModel(
                name = "Porsche 911 Dark Night",
                thumbnail = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?q=80&w=1000",
                templateType = "image",
                categoryId = "cars"
            ),
            TemplateDtoModel(
                name = "Supercar Headlights",
                thumbnail = "https://images.unsplash.com/photo-1525609004556-c46c7d6cf023?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1525609004556-c46c7d6cf023?q=80&w=1000",
                templateType = "image",
                categoryId = "cars"
            ),
            TemplateDtoModel(
                name = "Sports Car 4K",
                thumbnail = "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?q=80&w=1000",
                templateType = "image",
                categoryId = "cars"
            )
        ),
        "car" to listOf(
            TemplateDtoModel(
                name = "Porsche 911 Dark Night",
                thumbnail = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?q=80&w=1000",
                templateType = "image",
                categoryId = "cars"
            )
        ),
        "oled" to listOf(
            TemplateDtoModel(
                name = "OLED Black Minimalist",
                thumbnail = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=1000",
                templateType = "image",
                categoryId = "oled"
            ),
            TemplateDtoModel(
                name = "Dark Neon Abstract",
                thumbnail = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?q=80&w=1000",
                templateType = "image",
                categoryId = "oled"
            )
        ),
        "nature" to listOf(
            TemplateDtoModel(
                name = "Misty Forest Mountain",
                thumbnail = "https://images.unsplash.com/photo-1426604966848-d7adac402bff?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1426604966848-d7adac402bff?q=80&w=1000",
                templateType = "image",
                categoryId = "nature"
            ),
            TemplateDtoModel(
                name = "Valley Nature 4K",
                thumbnail = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?q=80&w=1000",
                imageModel = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?q=80&w=1000",
                templateType = "image",
                categoryId = "nature"
            )
        )
    )

    fun getWallpapersForTopic(query: String): List<TemplateDtoModel>? {
        val q = query.lowercase().trim()
        val matchedKey = themeMap.keys.firstOrNull { q.contains(it) || it.contains(q) }
        return if (matchedKey != null) themeMap[matchedKey] else null
    }
}
