package pion.tech.pionbase.feature.home

import android.view.View
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.adapter.InstallAppAdapter
import pion.tech.pionbase.feature.home.dialog.DemoDialog
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class,
    ),
    DemoDialog.Listener {
    //    val adapter = DemoMultipleAdapter()
    val adapter = InstallAppAdapter()

    override fun init(view: View) {
        initView()
        settingEvent()
        showDemoDialogEvent()
        onBackEvent()

        // Try to load templates for Template category when Home is initialized
        commonViewModel.loadTemplateFromTemplateCategoryName()
    }

    override fun subscribeObserver(view: View) {
        // Observe installed apps list
        viewModel.uiState
            .map { it.installedApps }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { installedApps ->
                adapter.submitList(installedApps)
            }

        // Observe loading state
        viewModel.uiState
            .map { it.isLoading }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isLoading ->
                showHideLoading(isLoading)
            }

        // Observe error state
        viewModel.uiState
            .map { it.error }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { error ->
                if (error != null) {
                    displayToast("Failed to load installed apps")
                }
            }

        // Observe CommonViewModel state for template loading if needed
        commonViewModel.uiState
            .map { it.templateState.isLoading }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isLoadingTemplate ->
                if (isLoadingTemplate) {
                    showHideLoading(true)
                }
            }
    }

    override fun onDialogPositiveClick() {
    }

    override fun onDialogNegativeClick() {
        displayToast("Hello")
    }
}
