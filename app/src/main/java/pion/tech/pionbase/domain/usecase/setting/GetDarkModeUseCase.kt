package pion.tech.pionbase.domain.usecase.setting

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.Result

class GetDarkModeUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    operator fun invoke(): Flow<Result<Int>> =
        dataStoreRepository.getDarkMode()
}
