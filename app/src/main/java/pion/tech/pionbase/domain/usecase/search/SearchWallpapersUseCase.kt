package pion.tech.pionbase.domain.usecase.search

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.data.repository.apiRepository.ApiRepository
import pion.tech.pionbase.util.Result

class SearchWallpapersUseCase(
    private val apiRepository: ApiRepository
) {
    operator fun invoke(query: String): Flow<Result<List<TemplateDtoModel>>> =
        apiRepository.searchWallpapers(query)
}
