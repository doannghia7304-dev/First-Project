package pion.tech.pionbase.data.repository.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.data.model.weather.WeatherCondition
import pion.tech.pionbase.data.model.weather.WeatherDtoModel
import pion.tech.pionbase.data.remote.WeatherApiInterface
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result
import timber.log.Timber
import java.util.Calendar

class WeatherRepositoryImpl(
    private val weatherApiInterface: WeatherApiInterface,
    private val context: Context
) : BaseRepository(), WeatherRepository {

    override fun getCurrentWeather(): Flow<Result<WeatherDtoModel>> = executeDataCall {
        try {
            var lat = DEFAULT_LATITUDE
            var lon = DEFAULT_LONGITUDE

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                val loc = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    ?: locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (loc != null) {
                    lat = loc.latitude
                    lon = loc.longitude
                }
            }

            val locationQuery = "$lat,$lon"
            val response = weatherApiInterface.getCurrentWeather(
                apiKey = BuildConfig.WEATHER_API_KEY,
                query = locationQuery
            )

            val conditionCode = response.current?.condition?.code ?: 0
            val conditionText = response.current?.condition?.text?.lowercase() ?: ""
            val isDay = response.current?.isDay == 1

            val condition = mapCondition(conditionCode, conditionText, isDay)
            val tempC = response.current?.tempC?.toInt()
            val locationName = response.location?.name ?: response.location?.region

            WeatherDtoModel(
                condition = condition,
                temperatureC = tempC,
                locationName = locationName,
                description = response.current?.condition?.text ?: DEFAULT_DESCRIPTION
            )
        } catch (e: Exception) {
            Timber.e(e, "Weather API call failed, using dynamic hour-based weather fallback")
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val fallbackCondition = if (hour in DAY_START_HOUR..DAY_END_HOUR) WeatherCondition.SUNNY else WeatherCondition.CLOUDY
            val fallbackTemp = when (hour) {
                in 11..15 -> 32
                in 6..10 -> 28
                in 16..18 -> 29
                else -> 24
            }
            WeatherDtoModel(
                condition = fallbackCondition,
                temperatureC = fallbackTemp,
                locationName = null,
                description = null
            )
        }
    }

    private fun mapCondition(code: Int, text: String, isDay: Boolean): WeatherCondition {
        return when {
            code == CODE_SUNNY -> if (isDay) WeatherCondition.SUNNY else WeatherCondition.CLOUDY
            code in THUNDERSTORM_CODES -> WeatherCondition.THUNDERSTORM
            code in RAINY_CODES -> WeatherCondition.RAINY
            code in SNOWY_CODES -> WeatherCondition.SNOWY
            code in CLOUDY_CODES -> WeatherCondition.CLOUDY
            text.contains("mưa") || text.contains("rain") || text.contains("drizzle") || text.contains("shower") -> WeatherCondition.RAINY
            text.contains("tuyết") || text.contains("snow") || text.contains("sleet") || text.contains("ice") || text.contains("blizzard") -> WeatherCondition.SNOWY
            text.contains("sấm") || text.contains("giông") || text.contains("thunder") || text.contains("storm") -> WeatherCondition.THUNDERSTORM
            text.contains("mây") || text.contains("cloud") || text.contains("fog") || text.contains("mist") || text.contains("u ám") || text.contains("âm u") -> WeatherCondition.CLOUDY
            text.contains("nắng") || text.contains("quang") || text.contains("clear") || text.contains("sunny") -> WeatherCondition.SUNNY
            else -> {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                if (hour in DAY_START_HOUR..DAY_END_HOUR) WeatherCondition.SUNNY else WeatherCondition.CLOUDY
            }
        }
    }

    companion object {
        private const val DEFAULT_LATITUDE = 21.0285 // Hanoi
        private const val DEFAULT_LONGITUDE = 105.8542 // Hanoi
        private const val DEFAULT_DESCRIPTION = "Clear"
        private const val DAY_START_HOUR = 6
        private const val DAY_END_HOUR = 17

        private const val CODE_SUNNY = 1000
        private val THUNDERSTORM_CODES = setOf(1087, 1273, 1276, 1279, 1282)
        private val RAINY_CODES = setOf(1063, 1180, 1183, 1186, 1189, 1192, 1195, 1198, 1201, 1240, 1243, 1246, 1249, 1252)
        private val SNOWY_CODES = setOf(1066, 1114, 1117, 1204, 1207, 1210, 1213, 1216, 1219, 1222, 1225, 1237, 1255, 1258, 1261, 1264)
        private val CLOUDY_CODES = setOf(1003, 1006, 1009, 1030, 1135, 1147)
    }
}
