package pion.tech.pionbase.feature.home

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.adapter.DemoMultipleAdapter
import pion.tech.pionbase.feature.home.dialog.DemoDialog
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel, CommonViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class.java,
        CommonViewModel::class.java,
    ),
    DemoDialog.Listener {
    val adapter = DemoMultipleAdapter()

    override fun init(view: View) {
        initView()
        plusEvent()
        settingEvent()
        onBackEvent()

        // Load installed apps
        viewModel.getInstalledApps()
    }

    override fun subscribeObserver(view: View) {
        // Observe installed apps state
        viewModel.installedAppsUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = {
                    showHideLoading(true)
                    logger.logEvent("installed_apps_loading")
                },
                onSuccess = { installedApps ->
                    showHideLoading(false)
                    logger.logEvent("installed_apps_loaded") {
                        putString("count", installedApps.size.toString())
                    }
                    // Handle the list of installed apps here
                    // You can update UI, show in RecyclerView, etc.
                },
                onError = {
                    showHideLoading(false)
                    logger.logEvent("installed_apps_error")
                    displayToast("Failed to load installed apps")
                },
            )
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
