package pion.tech.pionbase.feature.language

import android.view.View
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.language.LanguageUIModel
import pion.tech.pionbase.databinding.FragmentLanguageBinding
import pion.tech.pionbase.feature.language.adapter.LanguageAdapter
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class LanguageFragment :
    BaseFragment<FragmentLanguageBinding, LanguageViewModel>(
        FragmentLanguageBinding::inflate,
        LanguageViewModel::class.java,
    ),
    LanguageAdapter.Listener {
    val adapter = LanguageAdapter()

    override fun init(view: View) {
        initView()
        applyEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.languageData.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(onSuccess = { languages ->
                adapter.submitList(languages)
            })
        }
    }

    override fun onClickLanguage(
        item: LanguageUIModel,
        position: Int,
    ) {
        binding.ivDone.isVisible = true
        viewModel.selectLanguage(item)
    }
}
