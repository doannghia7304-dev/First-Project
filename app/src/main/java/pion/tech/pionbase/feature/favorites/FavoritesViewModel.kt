package pion.tech.pionbase.feature.favorites

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.database.entity.toDto
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.data.model.template.toPresentation
import pion.tech.pionbase.domain.usecase.favorite.AddFavoriteUseCase
import pion.tech.pionbase.domain.usecase.favorite.GetFavoritesUseCase
import pion.tech.pionbase.domain.usecase.favorite.RemoveFavoriteUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class FavoritesViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
) : BaseViewModel<FavoritesUiState, Nothing>(FavoritesUiState()) {

    init {
        getFavorites()
    }

    fun getFavorites() {
        setState { copy(favoritesState = UiState.Loading) }
        handleApiCall(
            apiCall = { getFavoritesUseCase() },
            onSuccess = { list ->
                val uiList = list.map { it.toPresentation() }
                setState { copy(favoritesState = UiState.Success(uiList)) }
            },
            onError = { throwable ->
                setState { copy(favoritesState = UiState.Error(throwable)) }
            }
        )
    }

    fun removeFavorite(item: TemplateUIModel) {
        handleApiCall(
            apiCall = { removeFavoriteUseCase(item.toDto()) },
            onSuccess = {
                getFavorites()
            }
        )
    }
}

data class FavoritesUiState(
    val favoritesState: UiState<List<TemplateUIModel>> = UiState.None
)
