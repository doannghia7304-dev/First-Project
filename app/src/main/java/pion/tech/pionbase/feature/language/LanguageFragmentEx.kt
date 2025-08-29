package pion.tech.pionbase.feature.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import pion.tech.pionbase.R
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClick
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun LanguageFragment.initView() {
    adapter.setListener(this)
    binding.rvMain.adapter = adapter
    binding.rvMain.setHasFixedSize(true)
    binding.ivBack.isVisible = isCameFromSetting()
}

fun LanguageFragment.applyEvent() {
    binding.ivDone.setPreventDoubleClick {
        if (viewModel.getSelectedLanguage() == null) {
            displayToast(getString(R.string.please_select_language))
            return@setPreventDoubleClick
        }
        if (isCameFromSetting()) {
            applySelectedLanguage()
            navigateToNextScreen()
            return@setPreventDoubleClick
        }
        applySelectedLanguage()
        navigateToNextScreen()
    }
}

fun LanguageFragment.applySelectedLanguage() {
    val locales = LocaleListCompat.forLanguageTags(viewModel.getSelectedLanguage()?.localeCode)
    AppCompatDelegate.setApplicationLocales(locales)
}

fun LanguageFragment.navigateToNextScreen() {
    if (isCameFromSetting()) {
        navigator.navigateTo(R.id.action_languageFragment_to_splashFragment)
    } else {
        navigator.navigateTo(R.id.action_languageFragment_to_onboardFragment)
    }
}

fun LanguageFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
    binding.ivBack.setPreventDoubleClickScaleView {
        backEvent()
    }
}

fun LanguageFragment.backEvent() {
    if (isCameFromSetting()) {
        findNavController().navigateUp()
    }
}

fun LanguageFragment.isCameFromSetting(): Boolean = navigator.isCameFrom(R.id.settingFragment)
