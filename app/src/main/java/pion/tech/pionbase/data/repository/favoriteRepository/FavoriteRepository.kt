package pion.tech.pionbase.data.repository.favoriteRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.util.Result

interface FavoriteRepository {
    fun getFavorites(): Flow<Result<List<TemplateDtoModel>>>
    fun addFavorite(wallpaper: TemplateDtoModel): Flow<Result<Unit>>
    fun removeFavorite(wallpaper: TemplateDtoModel): Flow<Result<Unit>>
    fun isFavorite(wallpaperId: String): Flow<Result<Boolean>>
}
