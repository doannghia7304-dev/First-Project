package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class SetStaticWallpaperPathUseCase(
    private val dataStoreRepository: DataStoreRepository
) : BaseUseCase() {

    operator fun invoke(path: String): Flow<Result<Unit>> =
        dataStoreRepository.setStaticWallpaperPath(path)
}
