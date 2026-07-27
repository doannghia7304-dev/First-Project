package pion.tech.pionbase.domain.usecase.language

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.language.LanguageDtoModel
import pion.tech.pionbase.data.repository.languageRepository.LanguageRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class GetLanguagesUseCase(
    private val languageRepository: LanguageRepository
) : BaseUseCase() {

    operator fun invoke(): Flow<Result<List<LanguageDtoModel>>> = executeFlow(dispatcher = Dispatchers.IO) {
        languageRepository.getLanguage()
    }
}
