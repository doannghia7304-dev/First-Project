package pion.tech.pionbase.feature.changeLanguage

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.utils.adsuntils.safePreloadAds
import pion.tech.pionbase.R
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.util.Constant
import pion.tech.pionbase.util.OnboardAds
import pion.tech.pionbase.util.OnboardFullAds
import timber.log.Timber
import kotlin.coroutines.resume

fun ChangeLanguageFragment.initView() {
    onSystemBack {
    }
}

fun ChangeLanguageFragment.preloadAndNav() {
    val tag = "preloadAndNav"

    val adsToLoad: List<Pair<String, String>> =
        listOf(
            OnboardAds.NATIVE_11_CONFIG to OnboardAds.NATIVE_11_SPACE1,
            OnboardAds.NATIVE_11_CONFIG to OnboardAds.NATIVE_11_SPACE2,
            OnboardFullAds.NATIVE_11_CONFIG to OnboardFullAds.NATIVE_11_SPACE1,
            OnboardFullAds.NATIVE_11_CONFIG to OnboardFullAds.NATIVE_11_SPACE2,
        )

    val adsTarget =
        setOf(
            OnboardAds.NATIVE_11_SPACE1,
            OnboardFullAds.NATIVE_11_SPACE1,
        )

    preloadJob =
        launchMain {
            var completionReason = "unknown"
            try {
                val result =
                    withTimeoutOrNull(Constant.maxTimeShowChangeLanguageScreen) {
                        val targetJobs = mutableListOf<Deferred<Boolean>>()
                        adsToLoad.forEach { (configName, spaceName) ->
                            val job =
                                async {
                                    loadAdSuspend(configName, spaceName)
                                }
                            if (spaceName in adsTarget) {
                                targetJobs.add(job)
                            }
                        }
                        targetJobs.awaitAll()
                    }

                completionReason =
                    if (result != null) {
                        "ads_target_loaded"
                    } else {
                        "timeout"
                    }
            } catch (e: Exception) {
                completionReason = "exception: ${e.message}"
                Timber.tag(tag).d(e, "exception occurred")
            } finally {
                Timber.tag(tag).d("goToNextScreen called - reason: $completionReason")
                goToNextScreen()
            }
        }
}

private suspend fun ChangeLanguageFragment.loadAdSuspend(
    configName: String,
    spaceName: String,
): Boolean =
    suspendCancellableCoroutine { cont ->
        safePreloadAds(
            configName = configName,
            spaceName = spaceName,
            includeHasBeenOpened = true,
            preloadCallback =
                object : PreloadCallback {
                    override fun onLoadDone() {
                        if (cont.isActive) cont.resume(true)
                    }

                    override fun onLoadFail(error: String) {
                        if (cont.isActive) cont.resume(false)
                    }
                },
        )
    }

fun ChangeLanguageFragment.goToNextScreen() {
    navigator.navigateTo(R.id.action_changeLanguageFragment_to_onboardFragment)
}

fun ChangeLanguageFragment.releaseAnimation() {
    progressAnimator?.cancel()
    progressAnimator = null
}
