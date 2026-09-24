package pion.tech.pionbase.feature.search

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.data.model.template.toPresentation
import pion.tech.pionbase.domain.usecase.search.ClearSearchHistoryUseCase
import pion.tech.pionbase.domain.usecase.search.GetSearchHistoryUseCase
import pion.tech.pionbase.domain.usecase.search.SaveSearchHistoryUseCase
import pion.tech.pionbase.domain.usecase.search.SearchWallpapersUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchWallpapersUseCase: SearchWallpapersUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val saveSearchHistoryUseCase: SaveSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : BaseViewModel<SearchUiState, SearchUiEvent>(SearchUiState()) {

    private val _searchQueryFlow = MutableStateFlow("")
    val searchQueryFlow = _searchQueryFlow.asStateFlow()

    init {
        loadSearchHistory()
        observeQueryChanges()
    }

    private fun loadSearchHistory() {
        handleApiCall(
            apiCall = { getSearchHistoryUseCase() },
            onSuccess = { history ->
                setState { copy(searchHistory = history) }
            }
        )
    }

    private fun observeQueryChanges() {
        _searchQueryFlow
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isBlank()) {
                    setState { copy(searchResults = UiState.None) }
                } else {
                    performSearch(query)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        _searchQueryFlow.value = query
        setState { copy(currentQuery = query) }
    }

    fun performSearch(query: String) {
        if (query.isBlank()) return
        setState { copy(searchResults = UiState.Loading) }
        handleApiCall(
            apiCall = { searchWallpapersUseCase(query) },
            onSuccess = { dtos ->
                setState { copy(searchResults = UiState.Success(dtos.map { it.toPresentation() })) }
                saveQueryHistory(query)
            },
            onError = { throwable ->
                setState { copy(searchResults = UiState.Error(throwable)) }
            }
        )
    }

    fun saveQueryHistory(query: String) {
        handleApiCall(
            apiCall = { saveSearchHistoryUseCase(query) },
            onSuccess = {
                loadSearchHistory()
            }
        )
    }

    fun clearSearchHistory() {
        handleApiCall(
            apiCall = { clearSearchHistoryUseCase() },
            onSuccess = {
                setState { copy(searchHistory = emptyList()) }
            }
        )
    }
}

data class SearchUiState(
    val currentQuery: String = "",
    val searchResults: UiState<List<TemplateUIModel>> = UiState.None,
    val searchHistory: List<String> = emptyList()
)

sealed interface SearchUiEvent
