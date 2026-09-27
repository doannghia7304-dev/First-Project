package pion.tech.pionbase.domain.usecase.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.weather.WeatherDtoModel
import pion.tech.pionbase.data.repository.weather.WeatherRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.util.Result

class GetCurrentWeatherUseCase(
    private val weatherRepository: WeatherRepository
) : BaseUseCase() {

    operator fun invoke(): Flow<Result<WeatherDtoModel>> = executeFlow(dispatcher = Dispatchers.IO) {
        weatherRepository.getCurrentWeather()
    }
}
