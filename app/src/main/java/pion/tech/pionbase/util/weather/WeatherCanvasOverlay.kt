package pion.tech.pionbase.util.weather

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.weather.WeatherCondition
import pion.tech.pionbase.data.model.weather.WeatherDtoModel

object WeatherCanvasOverlay {

    private const val CARD_BACKGROUND_COLOR = 0x4D000000 // Translucent black background

    fun drawWeatherOnBitmap(
        srcBitmap: Bitmap,
        isWeatherEnabled: Boolean,
        weather: WeatherDtoModel,
        context: Context? = null
    ): Bitmap {
        if (!isWeatherEnabled) return srcBitmap

        val bitmap = srcBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val canvasWidth = bitmap.width.toFloat()
        val canvasHeight = bitmap.height.toFloat()

        val condition = weather.condition ?: WeatherCondition.SUNNY
        val symbol = when (condition) {
            WeatherCondition.SUNNY -> "☀️"
            WeatherCondition.RAINY -> "🌧️"
            WeatherCondition.CLOUDY -> "☁️"
            WeatherCondition.SNOWY -> "❄️"
            WeatherCondition.THUNDERSTORM -> "🌩️"
        }

        val descText = context?.run {
            val resId = when (condition) {
                WeatherCondition.SUNNY -> R.string.weather_sunny
                WeatherCondition.CLOUDY -> R.string.weather_cloudy
                WeatherCondition.RAINY -> R.string.weather_rainy
                WeatherCondition.SNOWY -> R.string.weather_snowy
                WeatherCondition.THUNDERSTORM -> R.string.weather_thunderstorm
            }
            getString(resId)
        } ?: (weather.description ?: "")

        val locationNameText = context?.run {
            if (weather.locationName.isNullOrBlank()) {
                getString(R.string.current_location)
            } else {
                weather.locationName
            }
        } ?: (weather.locationName ?: "")

        val tempDisplay = if (weather.temperatureC != null) "${weather.temperatureC}°C" else ""
        val tempText = if (tempDisplay.isNotBlank()) "$symbol $tempDisplay" else symbol
        val locationText = if (descText.isNotBlank()) "$locationNameText • $descText" else locationNameText

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = canvasHeight * 0.035f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            setShadowLayer(8f, 0f, 2f, Color.BLACK)
        }

        val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = canvasHeight * 0.018f
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER
            setShadowLayer(6f, 0f, 2f, Color.BLACK)
        }

        val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = CARD_BACKGROUND_COLOR
            style = Paint.Style.FILL
        }

        // Draw top weather badge card
        val boxWidth = canvasWidth * 0.6f
        val boxHeight = canvasHeight * 0.09f
        val boxLeft = (canvasWidth - boxWidth) / 2f
        val boxTop = canvasHeight * 0.08f
        val rectBox = RectF(boxLeft, boxTop, boxLeft + boxWidth, boxTop + boxHeight)

        canvas.drawRoundRect(rectBox, 24f, 24f, boxPaint)

        val textCenterY = boxTop + (boxHeight / 2f)
        canvas.drawText(tempText, canvasWidth / 2f, textCenterY - 4f, titlePaint)
        canvas.drawText(locationText, canvasWidth / 2f, textCenterY + (titlePaint.textSize * 0.8f), subtitlePaint)

        return bitmap
    }
}
