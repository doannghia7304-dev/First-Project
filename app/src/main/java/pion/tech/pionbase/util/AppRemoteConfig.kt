package pion.tech.pionbase.util

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

object AppRemoteConfig {
    private object Defaults {
        const val MAX_TIME_SHOW_CHANGE_LANGUAGE = 5000L
    }

    var isRemoteConfigSuccess = false
        private set

    var maxTimeShowChangeLanguageScreen = Defaults.MAX_TIME_SHOW_CHANGE_LANGUAGE
        private set

    fun setRemoteConfigSuccess(isSuccess: Boolean) {
        isRemoteConfigSuccess = isSuccess
    }

    fun init(remoteConfig: FirebaseRemoteConfig) {
        maxTimeShowChangeLanguageScreen =
            remoteConfig.getLongSafe(
                "maxTimeShowChangeLanguageScreen",
                Defaults.MAX_TIME_SHOW_CHANGE_LANGUAGE,
            )
    }

    private fun FirebaseRemoteConfig.getLongSafe(
        key: String,
        default: Long,
    ): Long = runCatching { getLong(key) }.getOrDefault(default)

    private fun FirebaseRemoteConfig.getStringSafe(
        key: String,
        default: String,
    ): String = runCatching { getString(key) }.getOrDefault(default)

    private fun FirebaseRemoteConfig.getBooleanSafe(
        key: String,
        default: Boolean,
    ): Boolean = runCatching { getBoolean(key) }.getOrDefault(default)
}
