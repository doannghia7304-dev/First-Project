package pion.tech.pionbase.data.repository.weather

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.weather.WeatherDtoModel
import pion.tech.pionbase.util.Result

interface WeatherRepository {
    fun getCurrentWeather(): Flow<Result<WeatherDtoModel>>
}
