package pion.tech.pionbase.domain.usecase.setting

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.Result

class SetDarkModeUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    operator fun invoke(mode: Int): Flow<Result<Unit>> =
        dataStoreRepository.setDarkMode(mode)
}
