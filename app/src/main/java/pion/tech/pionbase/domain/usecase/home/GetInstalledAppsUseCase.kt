package pion.tech.pionbase.domain.usecase.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.installedApp.InstalledAppDtoModel
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class GetInstalledAppsUseCase(
    private val installedAppsRepository: InstalledAppsRepository,
) : BaseUseCase() {

    operator fun invoke(): Flow<Result<List<InstalledAppDtoModel>>> =
        executeFlow(dispatcher = Dispatchers.IO) {
            installedAppsRepository.getInstalledApps()
        }
}
