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
        private var animatedDrawable: AnimatedImageDrawable? = null
        private val handler = Handler(Looper.getMainLooper())
        private var isVisible = false
        private var observeJob: Job? = null

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
            observeJob = serviceScope.launch {
                dataStoreRepository.getLiveWallpaperPath().collect { result ->
                    if (result is Result.Success && result.data != null) {
                        val file = File(result.data)
                        if (file.exists()) {
                            updateGif(file)
                        }
                    }
                }
            }
            Timber.d("GifEngine created, starting DataStore observation")
        }

        override fun onDestroy() {
            observeJob?.cancel()
            super.onDestroy()
        }

        /**
         * Call this to update the GIF being displayed.
         */
        fun updateGif(file: File) {
            try {
                val source = ImageDecoder.createSource(file)
                val drawable = ImageDecoder.decodeDrawable(source)
                
                animatedDrawable?.stop()
                animatedDrawable?.callback = null
                
                if (drawable is AnimatedImageDrawable) {
                    animatedDrawable = drawable
                    animatedDrawable?.callback = callback
                    animatedDrawable?.repeatCount = AnimatedImageDrawable.REPEAT_INFINITE
                    if (isVisible) animatedDrawable?.start()
                    draw()
                } else {
                    Timber.w("Loaded drawable is not an AnimatedImageDrawable")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading GIF from ${file.absolutePath}")
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.isVisible = visible
            if (visible) {
                animatedDrawable?.start()
                draw()
            } else {
                animatedDrawable?.stop()
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            draw()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            isVisible = false
            animatedDrawable?.stop()
        }

        private fun draw() {
            if (!isVisible) return

            val canvas: Canvas? = surfaceHolder.lockCanvas()
            if (canvas != null) {
                try {
                    // Clear canvas with black
                    canvas.drawColor(Color.BLACK)

                    animatedDrawable?.let { drawable ->
                        // Calculate Aspect Fill
                        val canvasWidth = canvas.width.toFloat()
                        val canvasHeight = canvas.height.toFloat()
                        val drawableWidth = drawable.intrinsicWidth.toFloat()
                        val drawableHeight = drawable.intrinsicHeight.toFloat()

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
