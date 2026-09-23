package pion.tech.pionbase.feature.setting

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSettingBinding
import pion.tech.pionbase.util.collectFlowOnView

class SettingFragment :
    BaseFragment<FragmentSettingBinding, SettingViewModel>(
        FragmentSettingBinding::inflate,
        SettingViewModel::class,
    ) {
    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        applyEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.isCalendarOverlayEnabled }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isEnabled ->
                updateCalendarOverlayUI(isEnabled)
            }

        viewModel.uiState
            .map { it.calendarPosition }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { position ->
                updatePositionUI(position)
            }

        viewModel.uiState
            .map { it.calendarFontColor }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { color ->
                updateColorUI(color)
            }

        viewModel.uiState
            .map { it.darkMode }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { mode ->
                updateDarkModeUI(mode)
            }
    }
}
