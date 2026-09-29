package pion.tech.pionbase.data.model.weather

import com.google.gson.annotations.SerializedName

data class WeatherApiResponseDto(
    @SerializedName("location")
    val location: WeatherLocationDto? = null,
    @SerializedName("current")
    val current: WeatherCurrentDto? = null
)

data class WeatherLocationDto(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("region")
    val region: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lon")
    val lon: Double? = null
)

data class WeatherCurrentDto(
    @SerializedName("temp_c")
    val tempC: Float? = null,
    @SerializedName("temp_f")
    val tempF: Float? = null,
    @SerializedName("is_day")
    val isDay: Int? = null,
    @SerializedName("condition")
    val condition: WeatherConditionDto? = null,
    @SerializedName("wind_kph")
    val windKph: Float? = null,
    @SerializedName("humidity")
    val humidity: Int? = null,
    @SerializedName("cloud")
    val cloud: Int? = null,
    @SerializedName("feelslike_c")
    val feelsLikeC: Float? = null
)

data class WeatherConditionDto(
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("code")
    val code: Int? = null
)
