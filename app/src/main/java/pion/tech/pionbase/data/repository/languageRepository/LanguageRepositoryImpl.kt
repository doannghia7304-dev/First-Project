package pion.tech.pionbase.data.repository.languageRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.data.model.language.LanguageDtoModel
import pion.tech.pionbase.util.Result
import javax.inject.Inject

class LanguageRepositoryImpl
    @Inject
    constructor() : LanguageRepository {
        override fun getLanguage(): Flow<Result<List<LanguageDtoModel>>> {
            val listLanguageData =
                listOf(
                    LanguageDtoModel("https://flagcdn.com/w320/us.png", "English", "en"),
                    LanguageDtoModel("https://flagcdn.com/w320/es.png", "Español", "es"),
                    LanguageDtoModel("https://flagcdn.com/w320/sa.png", "عربي", "ar"),
                    LanguageDtoModel("https://flagcdn.com/w320/pt.png", "Português", "pt"),
                    LanguageDtoModel("https://flagcdn.com/w320/fr.png", "Français", "fr"),
                    LanguageDtoModel("https://flagcdn.com/w320/de.png", "Deutsch", "de"),
                    LanguageDtoModel("https://flagcdn.com/w320/cn.png", "中國人", "zh"),
                    LanguageDtoModel("https://flagcdn.com/w320/kr.png", "한국인", "ko"),
                    LanguageDtoModel("https://flagcdn.com/w320/jp.png", "日本人", "ja"),
                    LanguageDtoModel("https://flagcdn.com/w320/ru.png", "Pусский", "ru"),
                    LanguageDtoModel("https://flagcdn.com/w320/vn.png", "Việt Nam", "vi"),
                    LanguageDtoModel("https://flagcdn.com/w320/th.png", "ไทย", "th"),
                    LanguageDtoModel("https://flagcdn.com/w320/tr.png", "Türkçe", "tr"),
                    LanguageDtoModel("https://flagcdn.com/w320/in.png", "हिंदी", "hi"),
                    LanguageDtoModel("https://flagcdn.com/w320/uz.png", "O'zbek", "uz"),
                    LanguageDtoModel("https://flagcdn.com/w320/it.png", "Italiano", "it"),
                    LanguageDtoModel("https://flagcdn.com/w320/pl.png", "Polski", "pl"),
                    LanguageDtoModel("https://flagcdn.com/w320/ir.png", "فارسی", "fa"),
                    LanguageDtoModel("https://flagcdn.com/w320/ua.png", "Українська Мова", "uk"),
                    LanguageDtoModel("https://flagcdn.com/w320/ro.png", "Română", "ro"),
                    LanguageDtoModel("https://flagcdn.com/w320/nl.png", "Nederlands", "nl"),
                    LanguageDtoModel("https://flagcdn.com/w320/hu.png", "Magyar", "hu"),
                    LanguageDtoModel("https://flagcdn.com/w320/bg.png", "Български Език", "bg"),
                    LanguageDtoModel("https://flagcdn.com/w320/gr.png", "Ελληνικά", "el"),
                )
            return flow<Result<List<LanguageDtoModel>>> {
                emit(Result.Success(listLanguageData))
            }.catch {
                emit(Result.Error(it))
            }.flowOn(Dispatchers.IO)
        }
    }
