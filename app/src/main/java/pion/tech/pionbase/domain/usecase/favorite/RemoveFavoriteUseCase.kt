package pion.tech.pionbase.domain.usecase.favorite

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.data.repository.favoriteRepository.FavoriteRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class RemoveFavoriteUseCase(
    private val favoriteRepository: FavoriteRepository
) : BaseUseCase() {
    operator fun invoke(wallpaper: TemplateDtoModel): Flow<Result<Unit>> =
        favoriteRepository.removeFavorite(wallpaper)
}
