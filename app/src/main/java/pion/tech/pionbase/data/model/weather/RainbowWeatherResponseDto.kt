package pion.tech.pionbase.data.model.weather

import com.google.gson.annotations.SerializedName

data class RainbowWeatherResponseDto(
    @SerializedName("longitude")
    val longitude: Double? = null,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("temperature")
    val temperature: Float? = null,
    @SerializedName("temp")
    val temp: Float? = null,
    @SerializedName("temp_c")
    val tempC: Float? = null,
    @SerializedName("main")
    val main: MainTempDto? = null,
    @SerializedName("summary")
    val summary: RainbowSummaryDto? = null,
    @SerializedName("forecast")
    val forecast: List<RainbowForecastDto>? = null
)

data class MainTempDto(
    @SerializedName("temp")
    val temp: Float? = null
)

data class RainbowSummaryDto(
    @SerializedName("intensity")
    val intensity: String? = null
)

data class RainbowForecastDto(
    @SerializedName("timestampBegin")
    val timestampBegin: Long? = null,
    @SerializedName("timestampEnd")
    val timestampEnd: Long? = null,
    @SerializedName("precipRate")
    val precipRate: Float? = null,
    @SerializedName("precipType")
    val precipType: String? = null
)
