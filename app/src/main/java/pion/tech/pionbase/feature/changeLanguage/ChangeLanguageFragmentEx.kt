package pion.tech.pionbase.feature.changeLanguage

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import pion.tech.pionbase.R
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.util.AppRemoteConfig
import pion.tech.pionbase.util.OnboardAds
import pion.tech.pionbase.util.OnboardFullAds
import timber.log.Timber
import kotlin.coroutines.resume

fun ChangeLanguageFragment.initView() {
    onSystemBack {
    }
    startProgressAnimation()
}

fun ChangeLanguageFragment.startProgressAnimation() {
    progressAnimator =
        ValueAnimator.ofInt(0, 100).apply {
            duration = AppRemoteConfig.maxTimeShowChangeLanguageScreen
            interpolator = LinearInterpolator()
            addUpdateListener { animator ->
                runCatching {
                    val progress = animator.animatedValue as Int
                    binding.progressBar.progress = progress
                }
            }
            start()
        }
}

fun ChangeLanguageFragment.preloadAndNav() {
    val tag = "preloadAndNav"

    val adsToLoad: List<Pair<String, String>> =
        listOf(
            // Onboard 1
            OnboardAds.NATIVE_11_CONFIG to OnboardAds.NATIVE_11_SPACE1,
            OnboardAds.NATIVE_11_CONFIG to OnboardAds.NATIVE_11_SPACE2,
            // Onboard full 1
            OnboardFullAds.NATIVE_11_CONFIG to OnboardFullAds.NATIVE_11_SPACE1,
            OnboardFullAds.NATIVE_11_CONFIG to OnboardFullAds.NATIVE_11_SPACE2,
            // Onboard 2
            OnboardAds.NATIVE_2_CONFIG to OnboardAds.NATIVE_2_SPACE,
            // Onboard full 2
            OnboardFullAds.NATIVE_21_CONFIG to OnboardFullAds.NATIVE_21_SPACE1,
            OnboardFullAds.NATIVE_21_CONFIG to OnboardFullAds.NATIVE_21_SPACE2,
            // Onboard 3
            OnboardAds.NATIVE_2_CONFIG to OnboardAds.NATIVE_3_SPACE,
        )

    val adsTarget =
        setOf(
            OnboardAds.NATIVE_11_SPACE1,
            OnboardFullAds.NATIVE_11_SPACE1,
            OnboardAds.NATIVE_2_SPACE,
            OnboardFullAds.NATIVE_21_SPACE1,
            OnboardAds.NATIVE_3_SPACE,
        )

    preloadJob =
        launchMain {
            var completionReason = "unknown"
            try {
                val result =
                    withTimeoutOrNull(AppRemoteConfig.maxTimeShowChangeLanguageScreen) {
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
//        safePreloadAds(
//            configName = configName,
//            spaceName = spaceName,
//            includeHasBeenOpened = true,
//            preloadCallback =
//                object : PreloadCallback {
//                    override fun onLoadDone() {
//                        if (cont.isActive) cont.resume(true)
//                    }
//
//                    override fun onLoadFail(error: String) {
//                        if (cont.isActive) cont.resume(false)
//                    }
//                },
//        )
    }

fun ChangeLanguageFragment.goToNextScreen() {
    navigator.navigateTo(R.id.action_changeLanguageFragment_to_onboardFragment)
}

fun ChangeLanguageFragment.releaseAnimation() {
    progressAnimator?.cancel()
    progressAnimator = null
}
