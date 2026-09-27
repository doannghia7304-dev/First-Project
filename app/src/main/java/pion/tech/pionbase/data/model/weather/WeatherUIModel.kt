package pion.tech.pionbase.data.model.weather

data class WeatherUIModel(
    val condition: WeatherCondition? = null,
    val temperatureC: Int? = null,
    val locationName: String? = null,
    val description: String? = null
)

fun WeatherDtoModel.toPresentation() = WeatherUIModel(
    condition = this.condition,
    temperatureC = this.temperatureC,
    locationName = this.locationName,
    description = this.description
)
