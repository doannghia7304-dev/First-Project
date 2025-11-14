package pion.tech.pionbase.feature.language

import android.view.View
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.language.LanguageUIModel
import pion.tech.pionbase.databinding.FragmentLanguageBinding
import pion.tech.pionbase.feature.language.adapter.LanguageAdapter
import pion.tech.pionbase.util.collectFlowOnView

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
        viewModel.uiState
            .map { it.languages }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { languages ->
                adapter.submitList(languages)
            }

        viewModel.uiState
            .map { it.selectedLanguage }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { selectedLanguage ->
                binding.ivDone.isVisible = selectedLanguage != null
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
