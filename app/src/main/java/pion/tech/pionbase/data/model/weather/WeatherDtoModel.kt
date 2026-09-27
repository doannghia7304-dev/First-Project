package pion.tech.pionbase.data.model.weather

enum class WeatherCondition {
    SUNNY,
    RAINY,
    CLOUDY,
    SNOWY,
    THUNDERSTORM
}

data class WeatherDtoModel(
    val condition: WeatherCondition? = null,
    val temperatureC: Int? = null,
    val locationName: String? = null,
    val description: String? = null
)
