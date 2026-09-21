package pion.tech.pionbase.data.repository.favoriteRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.database.dao.FavoriteDao
import pion.tech.pionbase.data.database.entity.toDto
import pion.tech.pionbase.data.database.entity.toEntity
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result

class FavoriteRepositoryImpl(
    private val favoriteDao: FavoriteDao
) : BaseRepository(), FavoriteRepository {

    override fun getFavorites(): Flow<Result<List<TemplateDtoModel>>> =
        favoriteDao.getAllFavorites().executeDataWithFlowCall { list ->
            list.map { it.toDto() }
        }

    override fun addFavorite(wallpaper: TemplateDtoModel): Flow<Result<Unit>> =
        executeDataCall {
            favoriteDao.insertFavorite(wallpaper.toEntity())
        }

    override fun removeFavorite(wallpaper: TemplateDtoModel): Flow<Result<Unit>> =
        executeDataCall {
            favoriteDao.deleteFavorite(wallpaper.toEntity())
        }

    override fun isFavorite(wallpaperId: String): Flow<Result<Boolean>> =
        favoriteDao.isFavorite(wallpaperId).executeDataWithFlowCall { it }
}
