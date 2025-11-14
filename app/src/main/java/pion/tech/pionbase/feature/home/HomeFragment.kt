package pion.tech.pionbase.feature.home

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.adapter.InstallAppAdapter
import pion.tech.pionbase.feature.home.dialog.DemoDialog
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class.java,
    ),
    DemoDialog.Listener {
    //    val adapter = DemoMultipleAdapter()
    val adapter = InstallAppAdapter()

    override fun init(view: View) {
        initView()
        settingEvent()
        showDemoDialogEvent()
        onBackEvent()
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

        commonViewModel.getCategoryUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = { showHideLoading(true) },
                onSuccess = { listAppCategory ->
                    val templateCategoryId =
                        listAppCategory.firstOrNull { item -> item.name == "Template" }?.id
                    if (templateCategoryId != null) {
                        commonViewModel.getTemplate(templateCategoryId)
                    }
                },
                onError = { showHideLoading(false) },
            )
        }

        commonViewModel.getTemplateUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = { showHideLoading(true) },
                onSuccess = { showHideLoading(false) },
                onError = { showHideLoading(false) },
            )
        }
    }

    override fun onDialogPositiveClick() {
    }

    override fun onDialogNegativeClick() {
        displayToast("Hello")
    }
}
