package pion.tech.pionbase.service

import android.net.Uri
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
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

/**
 * A WallpaperService that renders a video MP4 using ExoPlayer
 */
class VideoWallpaperService : WallpaperService() {

    private val dataStoreRepository: DataStoreRepository by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreateEngine(): Engine {
        return VideoEngine()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    inner class VideoEngine : Engine() {
        private var exoPlayer: ExoPlayer? = null
        private var isVisible = false
        private var observeJob: Job? = null

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            Timber.d("VideoEngine created")
            initPlayer()
            observeWallpaperPath()
        }

        private fun initPlayer() {
            exoPlayer = ExoPlayer.Builder(this@VideoWallpaperService).build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                volume = 0f //
                playWhenReady = isVisible
            }
        }

        private fun observeWallpaperPath() {
            observeJob?.cancel()
            observeJob = serviceScope.launch {
                dataStoreRepository.getVideoWallpaperPath().collectLatest { result ->
                    if (result is Result.Success && result.data != null) {
                        val videoPath = result.data
                        val file = File(videoPath)
                        if (file.exists()) {
                            updateVideo(Uri.fromFile(file))
                        }
                    }
                }
            }
        }

        @OptIn(UnstableApi::class)
        private fun updateVideo(uri: Uri) {
            exoPlayer?.apply {
                setMediaItem(MediaItem.fromUri(uri))
                prepare()
                if (isVisible) play()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.isVisible = visible
            if (visible) {
                exoPlayer?.play()
            } else {
                exoPlayer?.pause()
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            exoPlayer?.setVideoSurfaceHolder(holder)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            exoPlayer?.clearVideoSurfaceHolder(holder)
        }

        override fun onDestroy() {
            observeJob?.cancel()
            exoPlayer?.release()
            exoPlayer = null
            super.onDestroy()
        }
    }
}
