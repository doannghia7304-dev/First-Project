package pion.tech.pionbase.domain.usecase.language

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class SetLanguageCodeUseCase(
    private val dataStoreRepository: DataStoreRepository
) : BaseUseCase() {

    operator fun invoke(code: String): Flow<Result<Unit>> = executeFlow(dispatcher = Dispatchers.IO) {
        dataStoreRepository.setLanguageCode(code)
    }
}
