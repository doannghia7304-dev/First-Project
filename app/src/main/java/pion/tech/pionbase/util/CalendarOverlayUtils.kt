package pion.tech.pionbase.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object CalendarOverlayUtils {

    fun drawCalendarOnBitmap(
        srcBitmap: Bitmap,
        isCalendarEnabled: Boolean,
        calendarPosition: Int,
        calendarFontColor: Int
    ): Bitmap {
        if (!isCalendarEnabled) return srcBitmap

        val bitmap = srcBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val canvasWidth = bitmap.width.toFloat()
        val canvasHeight = bitmap.height.toFloat()

        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

        val dayText = dayFormat.format(calendar.time).uppercase(Locale.getDefault())
        val dateText = dateFormat.format(calendar.time)

        val textColor = if (calendarFontColor != -1) calendarFontColor else Color.WHITE

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = canvasHeight * 0.032f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            setShadowLayer(8f, 0f, 2f, Color.BLACK)
        }

        val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = canvasHeight * 0.02f
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER
            setShadowLayer(6f, 0f, 2f, Color.BLACK)
        }

        val centerY = when (calendarPosition) {
            0 -> canvasHeight * 0.18f // Top
            1 -> canvasHeight * 0.50f // Center
            else -> canvasHeight * 0.78f // Bottom
        }

        canvas.drawText(dayText, canvasWidth / 2f, centerY, titlePaint)
        canvas.drawText(dateText, canvasWidth / 2f, centerY + titlePaint.textSize * 1.3f, subtitlePaint)

        return bitmap
    }
}
