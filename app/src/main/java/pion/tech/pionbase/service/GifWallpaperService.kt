package pion.tech.pionbase.service

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
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

        override fun onDestroy() {
            observeJob?.cancel()
            synchronized(drawLock) {
                (currentDrawable as? AnimatedImageDrawable)?.stop()
                currentDrawable?.callback = null
                currentDrawable = null
            }
            super.onDestroy()
        }

        fun updateContent(file: File) {
            try {
                val source = ImageDecoder.createSource(file)
                val drawable = ImageDecoder.decodeDrawable(source)
                
                synchronized(drawLock) {
                    (currentDrawable as? AnimatedImageDrawable)?.stop()
                    currentDrawable?.callback = null
                    
                    currentDrawable = drawable
                    currentDrawable?.callback = callback
                    
                    if (drawable is AnimatedImageDrawable) {
                        drawable.repeatCount = AnimatedImageDrawable.REPEAT_INFINITE
                        if (isVisible) drawable.start()
                    }
                }
                
                Timber.d("Content updated: ${file.name}")
                // Ép vẽ ngay lập tức
                draw()
                // Vẽ bù sau 100ms để chắc chắn Surface đã sẵn sàng
                handler.postDelayed({ draw() }, 100)
            } catch (e: Exception) {
                Timber.e(e, "Error loading content from ${file.absolutePath}")
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.isVisible = visible
            if (visible) {
                (currentDrawable as? AnimatedImageDrawable)?.start()
                draw()
            } else {
                (currentDrawable as? AnimatedImageDrawable)?.stop()
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
            (currentDrawable as? AnimatedImageDrawable)?.stop()
        }

        private fun draw() {
            synchronized(drawLock) {
                val canvas: Canvas? = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    try {
                        val canvasWidth = canvas.width.toFloat()
                        val canvasHeight = canvas.height.toFloat()
                        if (canvasWidth <= 0 || canvasHeight <= 0) return

                        canvas.drawColor(Color.BLACK)

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

                            val drawWidth = (drawableWidth * scale).toInt()
                            val drawHeight = (drawableHeight * scale).toInt()
                            val left = ((canvasWidth - drawWidth) / 2).toInt()
                            val top = ((canvasHeight - drawHeight) / 2).toInt()

                            drawable.setBounds(left, top, left + drawWidth, top + drawHeight)
                            drawable.draw(canvas)
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error drawing to canvas")
                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    }
                }
            }
        }
    }
}
