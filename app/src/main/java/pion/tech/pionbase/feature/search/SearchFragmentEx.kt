package pion.tech.pionbase.feature.search

import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import pion.tech.pionbase.util.setPreventDoubleClick

fun SearchFragment.initView() {
    historyAdapter.setListener(this)
    searchResultAdapter.setListener(this)
    binding.rvHistory.adapter = historyAdapter
    binding.rvSearchResults.adapter = searchResultAdapter
}

fun SearchFragment.applyEvent() {
    binding.etSearch.doAfterTextChanged { text ->
        val query = text?.toString() ?: ""
        binding.btnClearSearch.isVisible = query.isNotEmpty()
        viewModel.onQueryChanged(query)
    }

    binding.btnClearSearch.setPreventDoubleClick {
        binding.etSearch.setText("")
        viewModel.onQueryChanged("")
    }

    binding.btnClearHistory.setPreventDoubleClick {
        viewModel.clearSearchHistory()
    }

    fun onChipClick(chipText: String) {
        val query = chipText.replace(Regex("[^a-zA-Z0-9 ]"), "").trim()
        binding.etSearch.setText(query)
        binding.etSearch.setSelection(query.length)
        viewModel.onQueryChanged(query)
        viewModel.performSearch(query)
    }

    binding.chipCyberpunk.setPreventDoubleClick { onChipClick(binding.chipCyberpunk.text.toString()) }
    binding.chipDeepSea.setPreventDoubleClick { onChipClick(binding.chipDeepSea.text.toString()) }
    binding.chipSpace.setPreventDoubleClick { onChipClick(binding.chipSpace.text.toString()) }
    binding.chipAnime.setPreventDoubleClick { onChipClick(binding.chipAnime.text.toString()) }
    binding.chipCars.setPreventDoubleClick { onChipClick(binding.chipCars.text.toString()) }
    binding.chipOled.setPreventDoubleClick { onChipClick(binding.chipOled.text.toString()) }
    binding.chipNature.setPreventDoubleClick { onChipClick(binding.chipNature.text.toString()) }
}

fun SearchFragment.onBackEvent() {
    onSystemBack {
        navigator.navigateUp()
    }

    binding.btnBack.setPreventDoubleClick {
        navigator.navigateUp()
    }
}
