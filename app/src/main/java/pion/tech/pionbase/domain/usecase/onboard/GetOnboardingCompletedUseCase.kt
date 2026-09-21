package pion.tech.pionbase.domain.usecase.onboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class GetOnboardingCompletedUseCase(
    private val dataStoreRepository: DataStoreRepository
) : BaseUseCase() {

    operator fun invoke(): Flow<Result<Boolean>> = executeFlow(dispatcher = Dispatchers.IO) {
        dataStoreRepository.getIsOnboardingCompleted()
    }
}
