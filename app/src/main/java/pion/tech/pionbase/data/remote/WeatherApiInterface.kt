package pion.tech.pionbase.data.remote

import pion.tech.pionbase.data.model.weather.RainbowWeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherApiInterface {

    @GET("https://api.rainbow.ai/nowcast/v1/precip/{longitude}/{latitude}")
    suspend fun getRainbowPrecipitation(
        @Path("longitude") longitude: Double,
        @Path("latitude") latitude: Double
    ): RainbowWeatherResponseDto
}
