package pion.tech.pionbase.feature.setting

import android.os.Bundle
import android.view.View
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSettingBinding

class SettingFragment :
    BaseFragment<FragmentSettingBinding, SettingViewModel>(
        FragmentSettingBinding::inflate,
        SettingViewModel::class,
    ) {
    override fun init(view: View, savedInstanceState: Bundle?) {
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
