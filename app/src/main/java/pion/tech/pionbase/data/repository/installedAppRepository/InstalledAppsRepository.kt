package pion.tech.pionbase.data.repository.installedAppRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.installedApp.InstalledAppDtoModel
import pion.tech.pionbase.util.Result

interface InstalledAppsRepository {
    fun getInstalledApps(): Flow<Result<List<InstalledAppDtoModel>>>
}
