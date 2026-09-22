package pion.tech.pionbase.service

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Movie
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.text.TextPaint
import android.view.SurfaceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.Result
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GifWallpaperService : WallpaperService() {

    private val dataStoreRepository: DataStoreRepository by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreateEngine(): Engine {
        return GifEngine()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    inner class GifEngine : Engine() {
        private var currentDrawable: Drawable? = null
        @Suppress("DEPRECATION")
        private var currentMovie: Movie? = null
        private var movieStartTime: Long = 0L

        private var isCalendarEnabled = false
        private var calendarPosition = 2
        private var calendarFontColor = Color.WHITE

        private val handler = Handler(Looper.getMainLooper())
        private var isVisible = false
        private var observeJob: Job? = null
        private var surfaceWidth: Int = 0
        private var surfaceHeight: Int = 0
        private val drawLock = Any()

        private val callback = object : Drawable.Callback {
            override fun invalidateDrawable(who: Drawable) {
                draw()
            }

            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
                handler.postAtTime(what, `when`)
            }

            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                handler.removeCallbacks(what)
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            observeWallpaper()
            observeCalendarSettings()
            Timber.d("GifEngine created")
        }

        private fun observeWallpaper() {
            observeJob?.cancel()
            observeJob = serviceScope.launch {
                dataStoreRepository.getLiveWallpaperPath().collectLatest { result ->
                    if (result is Result.Success && result.data != null) {
                        val file = File(result.data)
                        if (file.exists()) {
                            updateContent(file)
                        }
                    }
                }
            }
        }

        private fun observeCalendarSettings() {
            serviceScope.launch {
                dataStoreRepository.getIsCalendarOverlayEnabled().collectLatest { result ->
                    if (result is Result.Success) {
                        isCalendarEnabled = result.data
                        draw()
                    }
                }
            }
            serviceScope.launch {
                dataStoreRepository.getCalendarPosition().collectLatest { result ->
                    if (result is Result.Success) {
                        calendarPosition = result.data
                        draw()
                    }
                }
            }
            serviceScope.launch {
                dataStoreRepository.getCalendarFontColor().collectLatest { result ->
                    if (result is Result.Success) {
                        calendarFontColor = if (result.data != -1) result.data else Color.WHITE
                        draw()
                    }
                }
            }
        }

        override fun onDestroy() {
            observeJob?.cancel()
            handler.removeCallbacksAndMessages(null)
            synchronized(drawLock) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    (currentDrawable as? AnimatedImageDrawable)?.stop()
                    currentDrawable?.callback = null
                    currentDrawable = null
                } else {
                    currentMovie = null
                }
            }
            super.onDestroy()
        }

        fun updateContent(file: File) {
            try {
                synchronized(drawLock) {
                    handler.removeCallbacksAndMessages(null)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        (currentDrawable as? AnimatedImageDrawable)?.stop()
                        currentDrawable?.callback = null

                        val source = ImageDecoder.createSource(file)
                        val drawable = ImageDecoder.decodeDrawable(source)
                        currentDrawable = drawable
                        currentDrawable?.callback = callback

                        if (drawable is AnimatedImageDrawable) {
                            drawable.repeatCount = AnimatedImageDrawable.REPEAT_INFINITE
                            if (isVisible) drawable.start()
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        currentMovie = Movie.decodeFile(file.absolutePath)
                        movieStartTime = 0L
                    }
                }

                Timber.d("Content updated: ${file.name}")
                draw()
                handler.postDelayed({ draw() }, 100)
            } catch (e: Exception) {
                Timber.e(e, "Error loading content from ${file.absolutePath}")
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.isVisible = visible
            if (visible) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    (currentDrawable as? AnimatedImageDrawable)?.start()
                }
                draw()
            } else {
                handler.removeCallbacksAndMessages(null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    (currentDrawable as? AnimatedImageDrawable)?.stop()
                }
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            this.surfaceWidth = holder?.surfaceFrame?.width()?.takeIf { it > 0 } ?: width
            this.surfaceHeight = holder?.surfaceFrame?.height()?.takeIf { it > 0 } ?: height
            Timber.d("Surface changed: ${this.surfaceWidth}x${this.surfaceHeight}")
            draw()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            isVisible = false
            surfaceWidth = 0
            surfaceHeight = 0
            handler.removeCallbacksAndMessages(null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                (currentDrawable as? AnimatedImageDrawable)?.stop()
            }
        }

        private fun draw() {
            if (!isVisible) return

            synchronized(drawLock) {
                val canvas: Canvas? = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    try {
                        val canvasWidth = canvas.width.toFloat()
                        val canvasHeight = canvas.height.toFloat()
                        if (canvasWidth <= 0 || canvasHeight <= 0) return

                        canvas.drawColor(Color.BLACK)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            currentDrawable?.let { drawable ->
                                var drawableWidth = drawable.intrinsicWidth.toFloat()
                                var drawableHeight = drawable.intrinsicHeight.toFloat()

                                if (drawableWidth <= 0 || drawableHeight <= 0) {
                                    drawableWidth = canvasWidth
                                    drawableHeight = canvasHeight
                                }

                                val scaleX = canvasWidth / drawableWidth
                                val scaleY = canvasHeight / drawableHeight
                                val scale = Math.max(scaleX, scaleY)

                                val scaledWidth = drawableWidth * scale
                                val scaledHeight = drawableHeight * scale
                                val dx = (canvasWidth - scaledWidth) / 2f
                                val dy = (canvasHeight - scaledHeight) / 2f

                                canvas.save()
                                canvas.translate(dx, dy)
                                canvas.scale(scale, scale)

                                drawable.setBounds(0, 0, drawableWidth.toInt(), drawableHeight.toInt())
                                drawable.draw(canvas)

                                canvas.restore()

                                handler.removeCallbacksAndMessages(null)
                                if (isVisible && drawable is AnimatedImageDrawable) {
                                    handler.postDelayed({ draw() }, 33)
                                }
                            }
                        } else {
                            @Suppress("DEPRECATION")
                            currentMovie?.let { movie ->
                                val now = SystemClock.uptimeMillis()
                                if (movieStartTime == 0L) movieStartTime = now
                                val duration = if (movie.duration() <= 0) 1000 else movie.duration()
                                var relTime = ((now - movieStartTime) % duration).toInt()
                                if (relTime == 0) relTime = 1
                                movie.setTime(relTime)

                                var movieWidth = movie.width().toFloat()
                                var movieHeight = movie.height().toFloat()
                                if (movieWidth <= 0 || movieHeight <= 0) {
                                    movieWidth = canvasWidth
                                    movieHeight = canvasHeight
                                }

                                val scaleX = canvasWidth / movieWidth
                                val scaleY = canvasHeight / movieHeight
                                val scale = Math.max(scaleX, scaleY)

                                val scaledWidth = movieWidth * scale
                                val scaledHeight = movieHeight * scale
                                val dx = (canvasWidth - scaledWidth) / 2f
                                val dy = (canvasHeight - scaledHeight) / 2f

                                canvas.save()
                                canvas.translate(dx, dy)
                                canvas.scale(scale, scale)
                                movie.draw(canvas, 0f, 0f)
                                canvas.restore()

                                handler.removeCallbacksAndMessages(null)
                                if (isVisible) {
                                    handler.postDelayed({ draw() }, 33)
                                }
                            }
                        }

                        // Draw Calendar Overlay on top if enabled
                        drawCalendarOverlay(canvas, canvasWidth, canvasHeight)
                    } catch (e: Exception) {
                        Timber.e(e, "Error drawing to canvas")
                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    }
                }
            }
        }

        private fun drawCalendarOverlay(canvas: Canvas, canvasWidth: Float, canvasHeight: Float) {
            if (!isCalendarEnabled) return

            val calendar = Calendar.getInstance()
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

            val dayText = dayFormat.format(calendar.time).uppercase(Locale.getDefault())
            val dateText = dateFormat.format(calendar.time)

            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = calendarFontColor
                textSize = canvasHeight * 0.032f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                setShadowLayer(8f, 0f, 2f, Color.BLACK)
            }

            val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = calendarFontColor
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
        }
    }
}
