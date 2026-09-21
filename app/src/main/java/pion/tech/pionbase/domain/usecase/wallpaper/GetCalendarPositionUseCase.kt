package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class GetCalendarPositionUseCase(
    private val dataStoreRepository: DataStoreRepository
) : BaseUseCase() {

    operator fun invoke(): Flow<Result<Int>> = executeFlow(dispatcher = Dispatchers.IO) {
        dataStoreRepository.getCalendarPosition()
    }
}
