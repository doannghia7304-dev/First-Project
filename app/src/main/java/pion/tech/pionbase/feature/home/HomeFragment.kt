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
import timber.log.Timber

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
                    Timber.tag("sagawgawgawggaw").d("chay vao loading")

                    showHideLoading(true)
                },
                onSuccess = { installedApps ->
                    Timber.tag("sagawgawgawggaw").d("chay vao onSuccess")

                    showHideLoading(false)
                },
                onError = {
                    Timber.tag("sagawgawgawggaw").d("chay vao onError")
                    showHideLoading(false)
                    displayToast("Failed to load installed apps")
                },
            )
        }

//        commonViewModel.getCategoryUiState.collectFlowOnView(viewLifecycleOwner) {
//            Timber.tag("sagawgawgawggaw").d("getCategoryUiState: ${it}")
//            it.handleUiState(
//                onLoading = { showHideLoading(true) },
//                onSuccess = { listAppCategory ->
//                    val templateCategoryId =
//                        listAppCategory.firstOrNull { item -> item.name == "Template" }?.id
//                    if (templateCategoryId != null) {
//                        commonViewModel.getTemplate(templateCategoryId)
//                    }
//                },
//                onError = { showHideLoading(false) },
//            )
//        }
//
//        commonViewModel.getTemplateUiState.collectFlowOnView(viewLifecycleOwner) {
//            Timber.tag("sagawgawgawggaw").d("getTemplateUiState: ${it}")
//            it.handleUiState(
//                onLoading = { showHideLoading(true) },
//                onSuccess = { showHideLoading(false) },
//                onError = { showHideLoading(false) },
//            )
//        }
    }

    override fun onDialogPositiveClick() {
    }

    override fun onDialogNegativeClick() {
        displayToast("Hello")
    }
}
