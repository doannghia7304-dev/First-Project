package pion.tech.pionbase.feature.setting

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSettingBinding

@AndroidEntryPoint
class SettingFragment :
    BaseFragment<FragmentSettingBinding, SettingViewModel>(
        FragmentSettingBinding::inflate,
        SettingViewModel::class.java,
    ) {
    override fun init(view: View) {
        backEvent()
        bindView()
        languageEvent()
        developerEvent()
        advertisementEvent()
        policyEvent()
        resetIapEvent()
        gdprEvent()
        resetGDPR()
    }

    override fun subscribeObserver(view: View) {
    }
}
