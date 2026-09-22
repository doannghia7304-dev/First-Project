package pion.tech.pionbase.domain.usecase.onboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class SetOnboardingCompletedUseCase(
    private val dataStoreRepository: DataStoreRepository
) : BaseUseCase() {

    operator fun invoke(isCompleted: Boolean): Flow<Result<Unit>> = executeFlow(dispatcher = Dispatchers.IO) {
        dataStoreRepository.setIsOnboardingCompleted(isCompleted)
    }
}
