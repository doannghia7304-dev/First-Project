package pion.tech.pionbase.feature.setting

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.net.toUri
import androidx.core.view.isVisible
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.feature.setting.dialog.AdvertisementDialog
import pion.tech.pionbase.feature.setting.dialog.DeveloperDialog
import pion.tech.pionbase.util.AppRemoteConfig
import pion.tech.pionbase.util.setPreventDoubleClick
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun SettingFragment.backEvent() {
    onSystemBack {
        onBackPressed()
    }
    binding.ivBack.setPreventDoubleClickScaleView {
        onBackPressed()
    }
}

fun SettingFragment.onBackPressed() {
    navigator.navigateUp()
}

@SuppressLint("SetTextI18n")
fun SettingFragment.bindView() {
    val remoteResult =
        if (AppRemoteConfig.isRemoteConfigSuccess) {
            "R"
        } else {
            "D"
        }

    binding.txvVersion.text =
        buildString {
            append("Application version: v")
            append(" ")
            append(remoteResult)
            append(" ")
            append(BuildConfig.VERSION_CODE)
            append(" ")
            append(BuildConfig.VERSION_NAME)
        }
}

fun SettingFragment.languageEvent() {
    binding.btnLanguage.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_settingFragment_to_languageFragment)
    }
}

fun SettingFragment.developerEvent() {
    binding.btnDeveloper.setPreventDoubleClickScaleView {
        DeveloperDialog().show(childFragmentManager)
    }
}

fun SettingFragment.advertisementEvent() {
    binding.btnAdvertisement.setPreventDoubleClickScaleView {
        AdvertisementDialog().show(childFragmentManager)
    }
}

fun SettingFragment.policyEvent() {
    binding.btnPolicy.setPreventDoubleClickScaleView {
        runCatching {
            val browserIntent =
                Intent(
                    Intent.ACTION_VIEW,
                    "https://sites.google.com/piontech.co/voicelockscreen".toUri(),
                )
            startActivity(browserIntent)
        }
    }
}

fun SettingFragment.resetIapEvent() {
    binding.btnResetIap.isVisible = BuildConfig.DEBUG
    binding.btnResetIap.setPreventDoubleClick {
        //TODO : check iap
//        IapController.resetIap(requireActivity())
    }
}

fun SettingFragment.gdprEvent() {
    binding.btnGdpr.setPreventDoubleClickScaleView {
        runCatching {
            //TODO : show policy form
//            AdsController.getInstance().showPolicyForm(
//                onShow = {
//                    // do nothing
//                },
//                onError = {
//                    // do nothing
//                },
//            )
        }
    }

    //TODO : check isNeedToShowConsent
//    if (AdsController.getInstance().isNeedToShowConsent() || BuildConfig.DEBUG) {
//        binding.btnGdpr.isVisible = true
//    } else {
//        binding.btnGdpr.isVisible = false
//    }
}

fun SettingFragment.resetGDPR() {
    if (BuildConfig.DEBUG) {
        binding.btnResetGdpr.isVisible = true
        binding.btnResetGdpr.setPreventDoubleClickScaleView {
            runCatching {
                //TODO : reset consent
//                AdsController.getInstance().resetConsent()
            }
        }
    } else {
        binding.btnResetGdpr.isVisible = false
    }
}
