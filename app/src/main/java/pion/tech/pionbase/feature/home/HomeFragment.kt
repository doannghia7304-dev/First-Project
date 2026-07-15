package pion.tech.pionbase.feature.home

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.adapter.InstallAppAdapter
import pion.tech.pionbase.feature.home.dialog.DemoDialog
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState
import timber.log.Timber

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class,
    ),
    DemoDialog.Listener,
    InstallAppAdapter.Listener {
    //    val adapter = DemoMultipleAdapter()
    val adapter = InstallAppAdapter()

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        settingEvent()
        showDemoDialogEvent()
        onBackEvent()

        // Try to load templates for Template category when Home is initialized
        apiViewModel.loadTemplateFromTemplateCategoryName()
    }

    override fun subscribeObserver(view: View) {
        // Observe installed apps list
        viewModel.uiState
            .map { it.installedAppsUiState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { uiState ->
                uiState.handleUiState(
                    onSuccess = { installedApps ->
                        adapter.submitList(installedApps)
                    },
                    onError = {
                        displayToast("Failed to load installed apps")
                    },
                    onLoading = {
                        // showHideLoading(true) - if needed
                    },
                    onNone = {
                        // showHideLoading(false) - if needed
                    }
                )
            }


        // Observe ApiViewModel state for template loading if needed
//        apiViewModel.uiState
//            .map { it.templateState.isLoading }
//            .distinctUntilChanged()
//            .collectFlowOnView(viewLifecycleOwner) { isLoadingTemplate ->
//                if (isLoadingTemplate) {
//                    showHideLoading(true)
//                }
//            }
    }

    override fun onDialogPositiveClick() {
    }

    override fun onDialogNegativeClick() {
        displayToast("Hello")
    }

    override fun onClickApp(item: InstalledAppUIModel) {
        val tag = "onClickApp"
        Timber.tag(tag).d("onClickApp: ${item.appName}")
    }
}
