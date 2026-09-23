package pion.tech.pionbase.feature.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.databinding.FragmentSearchBinding
import pion.tech.pionbase.feature.home.adapter.TemplateAdapter
import pion.tech.pionbase.feature.search.adapter.SearchHistoryAdapter
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.handleUiState

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>(
    FragmentSearchBinding::inflate,
    SearchViewModel::class
), SearchHistoryAdapter.Listener, TemplateAdapter.Listener {

    val historyAdapter = SearchHistoryAdapter()
    val searchResultAdapter = TemplateAdapter()

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        applyEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.searchHistory }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { history ->
                historyAdapter.submitList(history)
                binding.layoutHistory.isVisible = binding.etSearch.text.isNullOrEmpty() && history.isNotEmpty()
            }

        viewModel.uiState
            .map { it.searchResults }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { resultState ->
                val hasQuery = !binding.etSearch.text.isNullOrEmpty()
                resultState.handleUiState(
                    onLoading = {
                        showHideLoading(true)
                        binding.tvNoResults.isVisible = false
                    },
                    onSuccess = { templates ->
                        showHideLoading(false)
                        searchResultAdapter.submitList(templates)
                        binding.rvSearchResults.isVisible = hasQuery && templates.isNotEmpty()
                        binding.tvNoResults.isVisible = hasQuery && templates.isEmpty()
                        binding.layoutHistory.isVisible = !hasQuery && viewModel.uiState.value.searchHistory.isNotEmpty()
                    },
                    onError = {
                        showHideLoading(false)
                        binding.rvSearchResults.isVisible = false
                        binding.tvNoResults.isVisible = hasQuery
                    }
                )
            }
    }

    override fun onHistoryItemClick(query: String) {
        binding.etSearch.setText(query)
        binding.etSearch.setSelection(query.length)
        viewModel.onQueryChanged(query)
        viewModel.performSearch(query)
    }

    override fun onClickTemplate(item: TemplateUIModel, position: Int) {
        val bundle = Bundle().apply {
            putString("wallpaperPath", item.thumbnail)
            putBoolean("isVideo", item.videoPreview != null)
            putBoolean("isStatic", item.templateType == "image")
        }
        navigator.navigateTo(R.id.action_searchFragment_to_previewWallpaperFragment, bundle)
    }
}
