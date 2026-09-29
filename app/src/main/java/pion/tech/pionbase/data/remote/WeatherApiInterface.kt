package pion.tech.pionbase.data.remote

import pion.tech.pionbase.data.model.weather.WeatherApiResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiInterface {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("lang") lang: String = "vi"
    ): WeatherApiResponseDto
}
