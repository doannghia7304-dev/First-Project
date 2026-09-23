package pion.tech.pionbase.domain.usecase.language

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class GetLanguageCodeUseCase(
    private val dataStoreRepository: DataStoreRepository
) : BaseUseCase() {

    operator fun invoke(): Flow<Result<String?>> = executeFlow(dispatcher = Dispatchers.IO) {
        dataStoreRepository.getLanguageCode()
    }
}
