package pion.tech.pionbase.data.repository.languageRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.language.LanguageDtoModel
import pion.tech.pionbase.util.Result

interface LanguageRepository {
    fun getLanguage(): Flow<Result<List<LanguageDtoModel>>>
}
