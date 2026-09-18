package pion.tech.pionbase.util.calendar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import pion.tech.pionbase.R
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

/**
 * Utility class to overlay a monthly calendar grid onto a background wallpaper Bitmap using Canvas.
 * No hardcoded dimensions or colors; fully responsive and configurable via resources.
 */
object CalendarCanvasOverlay {

    data class CalendarConfig(
        val textColor: Int,
        val accentColor: Int,
        val backgroundColor: Int,
        val boxRadius: Float,
        val padding: Float,
        val marginHorizontal: Float,
        val marginVertical: Float,
        val titleTextSize: Float,
        val dayTextSize: Float,
        val titleTypeface: Typeface?,
        val dayTypeface: Typeface?
    )

    fun getDefaultConfig(context: Context): CalendarConfig {
        fun getDimen(name: String): Float {
            val id = context.resources.getIdentifier(name, "dimen", context.packageName)
            return if (id != 0) context.resources.getDimension(id) else 0f
        }

        return CalendarConfig(
            textColor = ContextCompat.getColor(context, R.color.white),
            accentColor = ContextCompat.getColor(context, R.color.purple_primary),
            backgroundColor = ContextCompat.getColor(context, R.color.calendar_box_background),
            boxRadius = getDimen("_12dp"),
            padding = getDimen("_16dp"),
            marginHorizontal = getDimen("_24dp"),
            marginVertical = getDimen("_60dp"),
            titleTextSize = getDimen("_16sp"),
            dayTextSize = getDimen("_12sp"),
            titleTypeface = ResourcesCompat.getFont(context, R.font.font_700),
            dayTypeface = ResourcesCompat.getFont(context, R.font.font_400)
        )
    }

    /**
     * Draws a responsive calendar grid overlay on top of the provided [sourceBitmap].
     * Returns a new mutable [Bitmap] containing the blended result.
     */
    fun createCalendarWallpaper(
        context: Context,
        sourceBitmap: Bitmap,
        config: CalendarConfig = getDefaultConfig(context)
    ): Bitmap {
        val width = sourceBitmap.width
        val height = sourceBitmap.height

        // Create a new mutable bitmap with the exact same dimensions
        val resultBitmap = Bitmap.createBitmap(width, height, sourceBitmap.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // 1. Draw original wallpaper background first
        canvas.drawBitmap(sourceBitmap, 0f, 0f, null)

        // 2. Setup standard Paints
        val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = config.backgroundColor
            style = Paint.Style.FILL
        }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = config.textColor
            textAlign = Paint.Align.CENTER
        }

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = config.textColor
            textSize = config.titleTextSize
            typeface = config.titleTypeface ?: Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }

        val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = config.accentColor
            style = Paint.Style.FILL
        }

        // 3. Get Current Calendar metadata
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        // Set calendar to the 1st of the current month to calculate day of week offset
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeekOffset = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Calendar Sunday = 1, we want 0-indexed
        val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // 4. Calculate Responsive Grid Boundaries
        val boxLeft = config.marginHorizontal
        val boxRight = width - config.marginHorizontal
        val boxWidth = boxRight - boxLeft
        
        // Dynamic row calculation (5 or 6 rows depending on month distribution)
        val totalCellsNeeded = firstDayOfWeekOffset + maxDaysInMonth
        val rowsCount = Math.ceil(totalCellsNeeded.toDouble() / 7.0).toInt()
        
        // Header heights
        val titleHeight = config.titleTextSize * 1.5f
        val daysOfWeekHeaderHeight = config.dayTextSize * 1.5f
        val cellHeight = config.dayTextSize * 2.0f
        
        val boxHeight = (config.padding * 2) + titleHeight + daysOfWeekHeaderHeight + (rowsCount * cellHeight)
        // Position calendar box at the bottom half of the screen dynamically
        val boxTop = height - boxHeight - config.marginVertical
        val boxBottom = boxTop + boxHeight

        // 5. Draw Translucent Translucent Container Box
        val rectBox = RectF(boxLeft, boxTop, boxRight, boxBottom)
        canvas.drawRoundRect(rectBox, config.boxRadius, config.boxRadius, boxPaint)

        // 6. Draw Month & Year Header Title
        val monthName = DateFormatSymbols(Locale.getDefault()).months[currentMonth]
        val headerText = "$monthName $currentYear"
        val titleY = boxTop + config.padding + config.titleTextSize
        canvas.drawText(headerText, boxLeft + (boxWidth / 2), titleY, titlePaint)

        // 7. Draw Weekday Labels Header (Sun, Mon, Tue, etc.)
        val shortWeekDays = DateFormatSymbols(Locale.getDefault()).shortWeekdays // Sunday is index 1
        val columnWidth = (boxWidth - (config.padding * 2)) / 7f
        val startGridX = boxLeft + config.padding
        val daysHeaderY = titleY + daysOfWeekHeaderHeight

        textPaint.textSize = config.dayTextSize
        textPaint.typeface = config.titleTypeface ?: Typeface.DEFAULT_BOLD

        for (i in 0 until 7) {
            val weekdayIndex = i + 1
            val dayName = shortWeekDays[weekdayIndex].uppercase(Locale.getDefault())
            val cellX = startGridX + (i * columnWidth) + (columnWidth / 2)
            canvas.drawText(dayName.take(3), cellX, daysHeaderY, textPaint)
        }

        // 8. Draw Days Grid Matrix
        textPaint.typeface = config.dayTypeface ?: Typeface.DEFAULT
        val gridTopY = daysHeaderY + (config.dayTextSize * 0.5f)

        var currentGridDay = 1
        for (row in 0 until rowsCount) {
            val cellY = gridTopY + (row * cellHeight) + (cellHeight / 2) + (config.dayTextSize / 3) // Adjusted baseline
            
            for (col in 0 until 7) {
                val cellIndex = (row * 7) + col
                
                if (cellIndex >= firstDayOfWeekOffset && currentGridDay <= maxDaysInMonth) {
                    val cellX = startGridX + (col * columnWidth) + (columnWidth / 2)
                    
                    // Highlighting current day today
                    if (currentGridDay == currentDay) {
                        val radius = config.dayTextSize * 0.9f
                        canvas.drawCircle(cellX, gridTopY + (row * cellHeight) + (cellHeight / 2), radius, accentPaint)
                        textPaint.typeface = config.titleTypeface ?: Typeface.DEFAULT_BOLD
                        textPaint.color = Color.WHITE
                    } else {
                        textPaint.typeface = config.dayTypeface ?: Typeface.DEFAULT
                        textPaint.color = config.textColor
                    }

                    canvas.drawText(currentGridDay.toString(), cellX, cellY, textPaint)
                    currentGridDay++
                }
            }
        }

        return resultBitmap
    }
}
