package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class SetActiveWallpaperTypeUseCase(
    private val dataStoreRepository: DataStoreRepository
) : BaseUseCase() {

    operator fun invoke(type: Int): Flow<Result<Unit>> =
        dataStoreRepository.setActiveWallpaperType(type)
}
