package pion.tech.pionbase.data.repository.installedAppRepository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.data.model.installedApp.InstalledAppDtoModel
import pion.tech.pionbase.util.Result

class InstalledAppsRepositoryImpl(
    @ApplicationContext private val context: Context,
) : InstalledAppsRepository {
    override fun getInstalledApps(): Flow<Result<List<InstalledAppDtoModel>>> =
        flow<Result<List<InstalledAppDtoModel>>> {
            val packageManager = context.packageManager
            val installedPackages =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            val apps =
                installedPackages
                    .map { appInfo ->
                        InstalledAppDtoModel(
                            packageName = appInfo.packageName,
                            appName =
                                try {
                                    packageManager.getApplicationLabel(appInfo).toString()
                                } catch (e: Exception) {
                                    appInfo.packageName
                                },
                            icon =
                                try {
                                    packageManager.getApplicationIcon(appInfo)
                                } catch (e: Exception) {
                                    null
                                },
                            versionName =
                                try {
                                    packageManager.getPackageInfo(appInfo.packageName, 0).versionName
                                } catch (e: Exception) {
                                    null
                                },
                            isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                        )
                    }.sortedBy { it.appName.lowercase() }

            emit(Result.Success(apps))
        }.catch {
            emit(Result.Error(it))
        }.flowOn(Dispatchers.IO)
}
