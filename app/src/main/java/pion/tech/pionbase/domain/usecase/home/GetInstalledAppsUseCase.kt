package pion.tech.pionbase.domain.usecase.home

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.installedApp.InstalledAppDtoModel
import pion.tech.pionbase.data.repository.installedAppRepository.InstalledAppsRepository
import pion.tech.pionbase.util.Result

class GetInstalledAppsUseCase(
    private val installedAppsRepository: InstalledAppsRepository
) {
    operator fun invoke(): Flow<Result<List<InstalledAppDtoModel>>> = installedAppsRepository.getInstalledApps()
}
