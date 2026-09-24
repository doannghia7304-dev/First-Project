package pion.tech.pionbase.domain.usecase.search

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.Result

class GetSearchHistoryUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    operator fun invoke(): Flow<Result<List<String>>> =
        dataStoreRepository.getSearchHistory()
}
