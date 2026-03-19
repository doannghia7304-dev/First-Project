package pion.tech.pionbase.data.model.installedApp

import android.graphics.drawable.Drawable

data class InstalledAppUIModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String?,
    val isSystemApp: Boolean,
)

fun InstalledAppDtoModel.toPresentation(): InstalledAppUIModel =
    InstalledAppUIModel(
        packageName = this.packageName,
        appName = this.appName,
        icon = this.icon,
        versionName = this.versionName,
        isSystemApp = this.isSystemApp,
    )
